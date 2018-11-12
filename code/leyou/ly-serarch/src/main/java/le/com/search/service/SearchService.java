package le.com.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.item.pojo.*;
import com.leyou.item.vo.SkuVo;
import le.com.search.client.BrandClient;
import le.com.search.client.CategoryClient;
import le.com.search.client.GoodsClient;
import le.com.search.client.SpecificationClient;
import le.com.search.pojo.Goods;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: cuzz
 * @Date: 2018/11/10 16:37
 * @Description:
 */
@Service
public class SearchService {
    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private BrandClient brandClient;

    public Goods buildGoods(Spu spu) {

        // 获取all字段的拼接
        String all = this.getall(spu);

        // 需要对sku过滤把不需要的数据去掉
        List<Sku> skus = goodsClient.querySkuBySpuId(spu.getId());
        List<SkuVo> skuVoList = this.getSkuVo(skus);

        // 获取sku的价格列表
        Set<Long> prices = this.getPrices(skus);

        // 获取specs
        // HashMap<String, Object> specs = getSpecs(spu); // TODO 数据不全导致的 bug

        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        // 搜索条件 拼接：标题、分类、品牌
        goods.setAll(all);
        goods.setPrice(prices);
        goods.setSkus(JsonUtils.serialize(skuVoList));
        // goods.setSpecs(specs); // TODO 数据不全导致的 bug

        return goods;
    }

    private HashMap<String,Object> getSpecs(Spu spu) {
        // 获取规格参数
        List<SpecParam> params = specificationClient.querySpecParams(null, spu.getCid3(), true, null);
        if (CollectionUtils.isEmpty(params)) {
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        // 查询商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spu.getId());
        // 获取通用规格参数
        //获取通用规格参数
        Map<Long, String> genericSpec = JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获取特有规格参数
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });

        //定义spec对应的map
        HashMap<String, Object> map = new HashMap<>();
        //对规格进行遍历，并封装spec，其中spec的key是规格参数的名称，值是商品详情中的值
        for (SpecParam param : params) {
            //key是规格参数的名称
            String key = param.getName();
            Object value = "";

            if (param.getGeneric()) {
                //参数是通用属性，通过规格参数的ID从商品详情存储的规格参数中查出值
                value = genericSpec.get(param.getId());
                if (param.getNumeric()) {
                    //参数是数值类型，处理成段，方便后期对数值类型进行范围过滤
                    value = chooseSegment(value.toString(), param);
                }
            } else {
                //参数不是通用类型
                value = specialSpec.get(param.getId());
            }
            value = (value == null ? "其他" : value);
            //存入map
            map.put(key, value);
        }
        return map;
    }

    private List<SkuVo> getSkuVo(List<Sku> skus) {
        List<SkuVo> skuVoList = new ArrayList<>();
        for (Sku sku : skus) {
            SkuVo skuVo = new SkuVo();
            skuVo.setId(sku.getId());
            skuVo.setPrice(sku.getPrice());
            skuVo.setTitle(sku.getTitle());
            skuVo.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            skuVoList.add(skuVo);
        }
        return skuVoList;

    }

    private Set<Long> getPrices(List<Sku> skuList) {
        // 查询sku
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        return skuList.stream().map(Sku::getPrice).collect(Collectors.toSet());
    }

    private String getall(Spu spu) {
        // 查询分类
        List<Category> categories = categoryClient.queryCategoryListByids(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(categories)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        // 查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        // 搜索字段
        String all = spu.getTitle() + StringUtils.join(names, ",") + brand.getName();
        return all;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
}
