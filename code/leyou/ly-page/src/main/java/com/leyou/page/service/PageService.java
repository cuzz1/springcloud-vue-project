package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {
    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecificationClient specificationClient;

    private static final Logger logger = LoggerFactory.getLogger(PageService.class);

    public Map<String, Object> loadModel(Long id) {
        try {
            // 模型数据
            Map<String, Object> modelMap = new HashMap<>();

            // 查询spu
            Spu spu = this.goodsClient.querySpuById(id);
            // 查询spuDetail
            SpuDetail detail = this.goodsClient.querySpuDetailById(id);
            // 查询sku
            List<Sku> skus = this.goodsClient.querySkuBySpuId(id);

            // 装填模型数据
            modelMap.put("spu", spu);
            modelMap.put("spuDetail", detail);
            modelMap.put("skus", skus);

            // 准备商品分类
            List<Category> categories = getCategories(spu);
            if (categories != null) {
                modelMap.put("categories", categories);
            }

            // 准备品牌数据
            List<Brand> brands = this.brandClient.queryBrandByIds(
                    Arrays.asList(spu.getBrandId()));
            modelMap.put("brand", brands.get(0));

            // 查询规格组及组内参数
            List<SpecGroup> groups = this.specificationClient.querySpecsByCid(spu.getCid3());
            modelMap.put("groups", groups);

            // 查询商品分类下的特有规格参数
            List<SpecParam> params =
                    this.specificationClient.querySpecParams(null, spu.getCid3(), null, false);
            // 处理成id:name格式的键值对
            Map<Long,String> paramMap = new HashMap<>();
            for (SpecParam param : params) {
                paramMap.put(param.getId(), param.getName());
            }
            modelMap.put("paramMap", paramMap);
            return modelMap;

        } catch (Exception e) {
            logger.error("加载商品数据出错,spuId:{}", id, e);
        }
        return null;
    }

    private List<Category> getCategories(Spu spu) {
        try {
            List<Category> categories = this.categoryClient.queryCategoryListByids(
                    Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            // Category c1 = new Category();
            // c1.setName(categories.get(0));
            // c1.setId(spu.getCid1());
            //
            // Category c2 = new Category();
            // c2.setName(names.get(1));
            // c2.setId(spu.getCid2());
            //
            // Category c3 = new Category();
            // c3.setName(names.get(2));
            // c3.setId(spu.getCid3());

            // return Arrays.asList(c1, c2, c3);
            return categories;
        } catch (Exception e) {
            logger.error("查询商品分类出错，spuId：{}", spu.getId(), e);
        }
        return null;
    }
}
