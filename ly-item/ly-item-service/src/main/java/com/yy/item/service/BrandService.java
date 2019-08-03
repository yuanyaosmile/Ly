package com.yy.item.service;

import com.leyou.item.pojo.Brand;
import com.ly.common.vo.PageResult;

import java.util.List;

public interface BrandService {
    PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key);

    void saveBrand(Brand brand, List<Long> cids);

    Brand queryById(Long id);

    List<Brand> queryBrandByCid(Long cid);

    List<Brand> queryBrandByIds(List<Long> ids);
}
