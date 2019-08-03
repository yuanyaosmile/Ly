package com.yy.item.web;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.ly.common.vo.PageResult;
import com.yy.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows,
            @RequestParam(value = "saleable", required = false)Boolean  saleable,
            @RequestParam(value = "key", required = false)String key){
        return ResponseEntity.ok(goodsService.querySpuByPage(page, rows, saleable, key));

    }

    /**
     * 商品新增
     * @param spu
     * @return
     */
    @PostMapping("save")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id") Long id){
        SpuDetail spuDetail = goodsService.querySpuDetailById(id);
        if (spuDetail == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(spuDetail);
    }

    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id){
        List<Sku> skus = goodsService.querySkuBySpuId(id);
        if (skus == null || skus.size() <= 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(skus);
    }

    @PutMapping
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu){
       try {
           goodsService.update(spu);
           return new ResponseEntity<>(HttpStatus.NO_CONTENT);
       }catch (Exception e){
           e.printStackTrace();
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
       }

    }
}
