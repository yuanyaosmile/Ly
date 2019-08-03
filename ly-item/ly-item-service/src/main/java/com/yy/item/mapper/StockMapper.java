package com.yy.item.mapper;

import com.leyou.item.pojo.Stock;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface StockMapper extends Mapper<Stock>, IdListMapper<Stock,Long>, InsertListMapper<Stock> {
}
