package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @Author: cuzz
 * @Date: 2018/11/6 19:38
 * @Description:
 */
@Data
@Table(name = "tb_spu")
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String title;// 标题
    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    private Boolean valid;// 是否有效，逻辑删除用
    private Date createTime;// 创建时间
    private Date lastUpdateTime;// 最后修改时间


    // 下面是数据库中没有的数据，本来是要放在vo中
    @Transient // 定该属性或字段不是永久的
    private String cname;// 商品分类名称
    @Transient
    private String bname;// 品牌名称
    @Transient
    private List<Sku> skus;
    @Transient
    private SpuDetail spuDetail;

}
