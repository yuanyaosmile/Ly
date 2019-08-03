package com.leyou.item.api;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.ly.common.vo.PageResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GoodsApi {
    /**
     *
     * @param id
     * @return
     */
    @GetMapping("goods/spu/detail/{id}")
    SpuDetail querySpuDetailById(@PathVariable("id") Long id);

    @GetMapping("goods/sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long id);

    @PutMapping
    Void updateGoods(@RequestBody Spu spu);

    @GetMapping("goods/spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows,
            @RequestParam(value = "saleable", required = false)Boolean  saleable,
            @RequestParam(value = "key", required = false)String key);
}
