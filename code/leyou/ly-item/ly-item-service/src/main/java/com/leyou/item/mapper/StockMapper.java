package com.leyou.item.mapper;

import com.leyou.item.pojo.Stock;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: cuzz
 * @Date: 2018/11/7 19:18
 * @Description: 必须导入tk.mybatis.mapper.additional.insert.InsertListMapper这个包
 */
public interface StockMapper extends Mapper<Stock>, IdListMapper<Stock, Long>, InsertListMapper<Stock> {
}
