package com.leyou.pojo;

import lombok.Data;

/**
 * @Author: cuzz
 * @Date: 2018/11/18 14:06
 * @Description:
 */
@Data
public class Cart {
    private Long userId;// 用户id
    private Long skuId;// 商品id
    private String title;// 标题
    private String image;// 图片
    private Long price;// 加入购物车时的价格
    private Integer num;// 购买数量
    private String ownSpec;// 商品规格参数
}
