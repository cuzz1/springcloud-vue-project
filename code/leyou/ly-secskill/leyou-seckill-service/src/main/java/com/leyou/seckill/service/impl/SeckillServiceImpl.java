package com.leyou.seckill.service.impl;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Stock;
import com.leyou.item.service.impl.GoodsServiceImpl;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.seckill.client.GoodsClient;
import com.leyou.seckill.client.OrderClient;
import com.leyou.seckill.mapper.SeckillMapper;
import com.leyou.seckill.mapper.SkuMapper;
import com.leyou.seckill.mapper.StockMapper;
import com.leyou.seckill.service.SeckillService;
import com.leyou.seckill.vo.SeckillGoods;
import com.leyou.seckill.vo.SeckillMessage;
import com.leyou.seckill.vo.SeckillParameter;
import com.leyou.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author: 98050
 * @Time: 2018-11-10 16:57
 * @Feature:
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(SeckillServiceImpl.class);


    /**
     * 添加秒杀商品
     * @param seckillParameter
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSeckillGoods(SeckillParameter seckillParameter) {


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        seckillParameter.setStartTime(calendar.getTime());
        calendar.add(Calendar.HOUR,2);
        seckillParameter.setEndTime(calendar.getTime());

        //1.根据spu_id查询商品
        Long id = seckillParameter.getId();
        Sku sku = goodsClient.querySkuById(id);
        //2.插入到秒杀商品表中
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setEnable(true);
        seckillGoods.setStartTime(seckillParameter.getStartTime());
        seckillGoods.setEndTime(seckillParameter.getEndTime());
        seckillGoods.setImage(sku.getImages());
        seckillGoods.setSkuId(sku.getId());
        seckillGoods.setStock(seckillParameter.getCount());
        seckillGoods.setTitle(sku.getTitle());
        seckillGoods.setSeckillPrice(sku.getPrice()*seckillParameter.getDiscount());
        this.seckillMapper.insert(seckillGoods);
        //3.更新对应的库存信息，tb_stock
        Stock stock = stockMapper.selectByPrimaryKey(sku.getId());
        stock.setSeckillStock(stock.getSeckillStock() + seckillParameter.getCount());
        stock.setSeckillTotal(stock.getSeckillTotal() + seckillParameter.getCount());
        stock.setStock(stock.getStock() - seckillParameter.getCount());
        this.stockMapper.updateByPrimaryKeySelective(stock);

    }

    /**
     * 查询秒杀商品
     * @return
     */
    @Override
    public List<SeckillGoods> querySeckillGoods() {
        Example example = new Example(SeckillGoods.class);
        example.createCriteria().andEqualTo("enable",true);
        List<SeckillGoods> list = this.seckillMapper.selectByExample(example);
        list.forEach(goods -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(goods.getSkuId());
            goods.setStock(stock.getSeckillStock());
            goods.setSeckillTotal(stock.getSeckillTotal());
        });
        return list;
    }

    /**
     * 创建订单
     * @param seckillGoods
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(SeckillGoods seckillGoods) {

        Order order = new Order();
        order.setPaymentType(1);
        order.setTotalPay(seckillGoods.getSeckillPrice());
        order.setActualPay(seckillGoods.getSeckillPrice());
        order.setPostFee(0+"");
        order.setReceiver("李四");
        order.setReceiverMobile("15812312312");
        order.setReceiverCity("西安");
        order.setReceiverDistrict("碑林区");
        order.setReceiverState("陕西");
        order.setReceiverZip("000000000");
        order.setInvoiceType(0);
        order.setSourceType(2);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setSkuId(seckillGoods.getSkuId());
        orderDetail.setNum(1);
        orderDetail.setTitle(seckillGoods.getTitle());
        orderDetail.setImage(seckillGoods.getImage());
        orderDetail.setPrice(seckillGoods.getSeckillPrice());
        orderDetail.setOwnSpec(this.skuMapper.selectByPrimaryKey(seckillGoods.getSkuId()).getOwnSpec());

        order.setOrderDetails(Arrays.asList(orderDetail));


        String seck = "seckill";
        ResponseEntity<List<Long>> responseEntity = this.orderClient.createOrder(seck,order);

        if (responseEntity.getStatusCode() == HttpStatus.OK){
            //库存不足，返回null
            return null;
        }
        return responseEntity.getBody().get(0);
    }

    /**
     * 检查秒杀库存
     * @param skuId
     * @return
     */
    @Override
    public boolean queryStock(Long skuId) {
        Stock stock = this.stockMapper.selectByPrimaryKey(skuId);
        if (stock.getSeckillStock() - 1 < 0){
            return false;
        }
        return true;
    }

    /**
     * 发送消息到秒杀队列当中
     * @param seckillMessage
     */
    @Override
    public void sendMessage(SeckillMessage seckillMessage) {
        String json = JsonUtils.serialize(seckillMessage);
        System.out.println(json);
        try {
            this.amqpTemplate.convertAndSend("order.seckill", json);
        }catch (Exception e){
            LOGGER.error("秒杀商品消息发送异常，商品id：{}",seckillMessage.getSeckillGoods().getSkuId(),e);
        }
    }
}
