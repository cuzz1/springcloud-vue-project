package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: cuzz
 * @Date: 2018/11/1 10:21
 * @Description:
 */
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category, Long>{
}
