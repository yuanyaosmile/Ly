package com.yy.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.pojo.*;
import com.ly.common.exception.GlobalException;
import com.ly.common.vo.CodeMsg;
import com.ly.common.vo.PageResult;
import com.yy.item.mapper.SkuMapper;
import com.yy.item.mapper.SpuDetailMapper;
import com.yy.item.mapper.SpuMapper;
import com.yy.item.mapper.StockMapper;
import com.yy.item.service.BrandService;
import com.yy.item.service.CategoryService;
import com.yy.item.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    SpuMapper  spuMapper;

    @Autowired
    SpuDetailMapper spuDetailMapper;

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    @Autowired
    SkuMapper skuMapper;

    @Autowired
    StockMapper stockMapper;

    @Override
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page, rows);

        //过滤
        Example example = new Example(Spu.class);

        Example.Criteria criteria = example.createCriteria();
        //搜索过滤字段
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }

        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        //默认排序
        example.setOrderByClause("last_update_time DESC");
        List<Spu> list = spuMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(list)) {
            throw new GlobalException(CodeMsg.GOODS_NOT_FOUND);
        }

        loadCategoryAndBrandName(list);
        //解析分页结果
        PageInfo<Spu> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(), list);
    }

    @Override
    @Transactional
    public void saveGoods(Spu spu) {
        //新增spu
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);

        int count = spuMapper.insert(spu);
        if (count != 1) {
            throw new GlobalException(CodeMsg.SAVE_GOODS_FAILED);
        }
        //新增detail
        SpuDetail detail = spu.getSpuDetail();
        detail.setSpuId(spu.getId());
        spuDetailMapper.insert(detail);

        List<Stock> stocks = new ArrayList<>();

        //新增sku
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(sku.getSpuId());

            count = skuMapper.insert(sku);
            if (count != 1) {
                throw new GlobalException(CodeMsg.SAVE_GOODS_FAILED);
            }
            //新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            stocks.add(stock);
        }
        //批量新增
        stockMapper.insertList(stocks);

    }

    @Override
    public SpuDetail querySpuDetailById(Long id) {
        return spuDetailMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(record);
        for (Sku sku : skus) {
            //查询库存
            sku.setStock(stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        }
        return skus;
    }

    @Override
    @Transactional
    public void update(Spu spu) {
        //查询以前的sku
        List<Sku> skus = this.querySkuBySpuId(spu.getId());
        //如果以前存在，那么删除
        if (!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(sku -> sku.getId()).collect(Collectors.toList());
            //删除以前的库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            stockMapper.deleteByExample(example);

            //删除以前的sku
            Sku record = new Sku();
            record.setSpuId(spu.getId());
            skuMapper.delete(record);
        }
        //新增sku和库存
        saveSkuAndStock(spu.getSkus(), spu.getId());

        //更新spu
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        spu.setValid(null);
        spu.setSaleable(null);
        spuMapper.updateByPrimaryKey(spu);
        //更新spu详情
        spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
    }

    private void saveSkuAndStock(List<Sku> skus, Long spuId) {
        for (Sku sku : skus) {
            if (!sku.getEnable()) {
                continue;
            }
            sku.setSpuId(spuId);
            sku.setCreateTime(new Date());
            skuMapper.insert(sku);

            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insert(stock);
        }
    }

    private void loadCategoryAndBrandName(List<Spu> spus){
        for (Spu spu : spus) {
            //处理分类名称
            List<String> name = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())).stream().map(Category::getName)
                    .collect(Collectors.toList());
            spu.setCname(StringUtils.join(name, "/"));

            //处理品牌名称
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }
    }
}
