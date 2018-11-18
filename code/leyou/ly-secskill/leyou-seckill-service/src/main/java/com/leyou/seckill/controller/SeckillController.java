package com.leyou.seckill.controller;


import com.leyou.auth.entity.UserInfo;
import com.leyou.seckill.interceptor.LoginInterceptor;
import com.leyou.seckill.service.SeckillService;

import com.leyou.seckill.vo.SeckillGoods;
import com.leyou.seckill.vo.SeckillMessage;
import com.leyou.seckill.vo.SeckillParameter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Author: 98050
 * @Time: 2018-11-10 16:57
 * @Feature:
 */
@RestController
@RequestMapping
public class SeckillController implements InitializingBean {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String KEY_PREFIX = "leyou:seckill:stock";

    /**
     * 添加秒杀商品(后台)
     * @param seckillParameter
     * @return
     */
    @PostMapping("addSeckill")
    public ResponseEntity<Boolean> addSeckillGoods(@RequestBody SeckillParameter seckillParameter){
        if (seckillParameter != null){
            this.seckillService.addSeckillGoods(seckillParameter);
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 查询秒杀商品
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<SeckillGoods>> querySeckillGoods(){
        List<SeckillGoods> list = this.seckillService.querySeckillGoods();
        if (list == null || list.size() < 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(list);
    }


    @PostMapping("seck")
    public ResponseEntity<String> seckillOrder(@RequestBody SeckillGoods seckillGoods){

        //1.读取库存，减一后更新缓存
        BoundHashOperations<String,Object,Object> hashOperations = this.stringRedisTemplate.boundHashOps(KEY_PREFIX);
        String s = (String) hashOperations.get(seckillGoods.getSkuId().toString());
        int stock = Integer.valueOf(s) - 1;
        hashOperations.delete(seckillGoods.getSkuId().toString());
        hashOperations.put(seckillGoods.getSkuId().toString(),String.valueOf(stock));
        //2.库存不足直接返回
        if (stock < 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        //3.库存充足，请求入队
        //3.1 获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        SeckillMessage seckillMessage = new SeckillMessage(userInfo,seckillGoods);
        this.seckillService.sendMessage(seckillMessage);



//        //检查库存
//        if (!this.seckillService.queryStock(seckillGoods.getSkuId())){
//            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
//        }
//
//        //1.创建订单
//        Long id = this.seckillService.createOrder(seckillGoods);
//        //2.判断秒杀是否成功
//        if (id == null){
//           return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
//        }
        String result = "排队中";
        return ResponseEntity.ok(result);
    }

    /**
     * 初始化秒杀商品数量
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //1.查询可以秒杀的商品
        List<SeckillGoods> seckillGoods = this.seckillService.querySeckillGoods();
        if (seckillGoods == null || seckillGoods.size() == 0){
            return;
        }
        BoundHashOperations<String,Object,Object> hashOperations = this.stringRedisTemplate.boundHashOps(KEY_PREFIX);
        seckillGoods.forEach(goods -> {
            hashOperations.put(goods.getSkuId().toString(),goods.getStock().toString());
        });
    }
}
