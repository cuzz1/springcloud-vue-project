package com.leyou.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.auth.entity.UserInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Stock;
import com.leyou.order.interceptor.LoginInterceptor;
import com.leyou.order.mapper.*;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.pojo.SeckillOrder;
import com.leyou.order.service.OrderService;
import com.leyou.utils.IdWorker;
import com.leyou.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.sound.midi.SoundbankResource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 98050
 * @Time: 2018-10-27 16:37
 * @Feature:
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createOrder(String tag,Order order) {
        String seck = "seckill";
        boolean create = true;
        //1.先判断订单中的商品库存是否充足
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Stock stock = this.stockMapper.selectByPrimaryKey(orderDetail.getSkuId());
            if (stock.getStock() - orderDetail.getNum() < 0) {
                create = false;
            } else if (tag.equals(seck) && stock.getSeckillStock() - orderDetail.getNum() < 0) {
                create = false;
            }
        }
        if (create) {
            //创建订单
            //1.生成orderId
            long orderId = idWorker.nextId();
            //2.获取登录的用户
            UserInfo userInfo = LoginInterceptor.getLoginUser();
            //3.初始化数据
            order.setBuyerNick(userInfo.getUsername());
            order.setBuyerRate(false);
            order.setCreateTime(new Date());
            order.setOrderId(orderId);
            order.setUserId(userInfo.getId());
            //4.保存数据
            this.orderMapper.insertSelective(order);

            //5.保存订单状态
            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setOrderId(orderId);
            orderStatus.setCreateTime(order.getCreateTime());
            //初始状态未未付款：1
            orderStatus.setStatus(1);
            //6.保存数据
            this.orderStatusMapper.insertSelective(orderStatus);

            //7.在订单详情中添加orderId
            order.getOrderDetails().forEach(orderDetail -> {
                //添加订单
                orderDetail.setOrderId(orderId);
            });

            //8.保存订单详情，使用批量插入功能
            this.orderDetailMapper.insertList(order.getOrderDetails());

            //判断是否是秒杀订单
            if (StringUtils.isNotEmpty(tag) && tag.equals(seck)) {
                order.getOrderDetails().forEach(orderDetail -> {
                    Stock stock = this.stockMapper.selectByPrimaryKey(orderDetail.getSkuId());
                    stock.setStock(stock.getStock() - orderDetail.getNum());
                    stock.setSeckillStock(stock.getSeckillStock() - orderDetail.getNum());
                    this.stockMapper.updateByPrimaryKeySelective(stock);

                    //新建秒杀订单
                    SeckillOrder seckillOrder = new SeckillOrder();
                    seckillOrder.setOrderId(orderId);
                    seckillOrder.setSkuId(orderDetail.getSkuId());
                    seckillOrder.setUserId(userInfo.getId());
                    this.seckillOrderMapper.insert(seckillOrder);

                });
            } else {
                //普通订单
                order.getOrderDetails().forEach(orderDetail -> this.stockMapper.reduceStock(orderDetail.getSkuId(), orderDetail.getNum()));
            }
            return orderId;
        } else {
            return null;
        }

    }

    /**
     * 根据订单号查询订单
     * @param id
     * @return
     */
    @Override
    public Order queryOrderById(Long id) {
        //1.查询订单
        Order order = this.orderMapper.selectByPrimaryKey(id);
        //2.查询订单详情
        Example example = new Example(OrderDetail.class);
        example.createCriteria().andEqualTo("orderId",id);
        List<OrderDetail> orderDetail = this.orderDetailMapper.selectByExample(example);
        orderDetail.forEach(System.out::println);
        //3.查询订单状态
        OrderStatus orderStatus = this.orderStatusMapper.selectByPrimaryKey(order.getOrderId());
        //4.order对象填充订单详情
        order.setOrderDetails(orderDetail);
        //5.order对象设置订单状态
        order.setStatus(orderStatus.getStatus());
        //6.返回order
        return order;
    }

    /**
     * 查询当前登录用户的订单，通过订单状态进行筛选
     * @param page
     * @param rows
     * @param status
     * @return
     */
    @Override
    public PageResult<Order> queryUserOrderList(Integer page, Integer rows, Integer status) {
        try{
            //1.分页
            PageHelper.startPage(page,rows);
            //2.获取登录用户
            UserInfo userInfo = LoginInterceptor.getLoginUser();
            //3.查询
            Page<Order> pageInfo = (Page<Order>) this.orderMapper.queryOrderList(userInfo.getId(), status);
            //4.填充orderDetail
            List<Order> orderList = pageInfo.getResult();
            orderList.forEach(order -> {
                Example example = new Example(OrderDetail.class);
                example.createCriteria().andEqualTo("orderId",order.getOrderId());
                List<OrderDetail> orderDetailList = this.orderDetailMapper.selectByExample(example);
                order.setOrderDetails(orderDetailList);
            });
            return new PageResult<>(pageInfo.getTotal(),(long)pageInfo.getPages(), orderList);
        }catch (Exception e){
            logger.error("查询订单出错",e);
            return null;
        }
    }

    /**
     * 更新订单状态
     * @param id
     * @param status
     * @return
     */
    @Override
    public Boolean updateOrderStatus(Long id, Integer status) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(id);
        orderStatus.setStatus(status);
        //1.根据状态判断要修改的时间
        switch (status){
            case 2:
                //2.付款时间
                orderStatus.setPaymentTime(new Date());
                break;
            case 3:
                //3.发货时间
                orderStatus.setConsignTime(new Date());
                break;
            case 4:
                //4.确认收货，订单结束
                orderStatus.setEndTime(new Date());
                break;
            case 5:
                //5.交易失败，订单关闭
                orderStatus.setCloseTime(new Date());
                break;
            case 6:
                //6.评价时间
                orderStatus.setCommentTime(new Date());
                break;

                default:
                    return null;
        }
        int count = this.orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
        return count == 1;
    }

    /**
     * 根据订单号查询商品id
     * @param id
     * @return
     */
    @Override
    public List<Long> querySkuIdByOrderId(Long id) {
        Example example = new Example(OrderDetail.class);
        example.createCriteria().andEqualTo("orderId",id);
        List<OrderDetail> orderDetailList = this.orderDetailMapper.selectByExample(example);
        List<Long> ids = new ArrayList<>();
        orderDetailList.forEach(orderDetail -> ids.add(orderDetail.getSkuId()));
        return ids;
    }

    /**
     * 根据订单号查询订单状态
     * @param id
     * @return
     */
    @Override
    public OrderStatus queryOrderStatusById(Long id) {
        return this.orderStatusMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询订单下商品的库存，返回库存不足的商品Id
     * @param order
     * @return
     */
    @Override
    public List<Long> queryStock(String tag,Order order) {
        String seck = "seckill";
        List<Long> skuId = new ArrayList<>();
        order.getOrderDetails().forEach(orderDetail -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(orderDetail.getSkuId());
            if (stock.getStock() - orderDetail.getNum() < 0){
                //先判断库存是否充足
                skuId.add(orderDetail.getSkuId());
            }else{
                //充足的话就判断秒杀库存是否充足
                if (StringUtils.isNotEmpty(tag) && seck.equals(tag)){
                    //检查秒杀库存
                    if (stock.getSeckillStock() - orderDetail.getNum() < 0){
                        //不充足
                        skuId.add(orderDetail.getSkuId());
                    }
                }
            }
        });
        return skuId;
    }

}
