package com.yy.item.service;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.ly.common.vo.PageResult;

import java.util.List;

public interface GoodsService {
    PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key);

    void saveGoods(Spu spu);

    SpuDetail querySpuDetailById(Long id);

    List<Sku> querySkuBySpuId(Long id);

    void update(Spu spu);
}
