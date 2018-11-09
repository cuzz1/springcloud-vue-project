package com.leyou.item.api;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: cuzz
 * @Date: 2018/11/9 16:03
 * @Description:
 */
public interface GoodsApi {
    /**
     * 分页查询品牌
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     */
    @GetMapping("page")
    PageResult<Brand> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key);

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @PostMapping
    // 传入 "1,2,3"的字符串可以解析为列表
    Void saveBrand(Brand brand, @RequestParam("cids") List<Long> cids);

    /**
     * 根据分类查询品牌
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    List<Brand> queryBrandByCategory(@PathVariable("cid") Long cid);

    /**
     * 根据id查询品牌
     * @param id
     */
    @GetMapping("{id}")
    Brand queryBrandById(@PathVariable("id") Long id);
}
