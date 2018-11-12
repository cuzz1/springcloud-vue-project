package com.leyou.item.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: cuzz
 * @Date: 2018/11/10 17:56
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuVo {

    private Long id;
    private String title;
    private Long price;
    private String image;
}
