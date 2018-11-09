package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: cuzz
 * @Date: 2018/11/9 16:08
 * @Description:
 */
public interface BrandApi {

    /**
     * 根据id查询品牌
     * @param id
     */
    @GetMapping("brand/{id}")
    Brand queryBrandById(@PathVariable("id") Long id);
}
