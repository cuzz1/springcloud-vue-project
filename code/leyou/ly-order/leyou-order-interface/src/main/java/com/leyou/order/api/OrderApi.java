package com.leyou.order.api;

import com.leyou.order.pojo.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: 98050
 * @Time: 2018-11-12 15:13
 * @Feature: 订单服务接口
 */
@RequestMapping("order")
public interface OrderApi {

    /**
     * 创建订单
     * @param seck
     * @param order
     * @return
     */
    @PostMapping
    ResponseEntity<List<Long>> createOrder(@RequestParam("seck") String seck, @RequestBody @Valid Order order);
}
