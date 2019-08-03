package com.yy.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.pojo.Brand;
import com.ly.common.exception.GlobalException;
import com.ly.common.vo.CodeMsg;
import com.ly.common.vo.PageResult;
import com.yy.item.mapper.BrandMapper;
import com.yy.item.service.BrandService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    BrandMapper brandMapper;

    @Override
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //分页
        PageHelper.startPage(page, rows);
        //过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            //过滤条件
            example.createCriteria().orLike("name", "%" + key + "%")
                    .orEqualTo("letter", key.toUpperCase());
        }
        //排序
        if (StringUtils.isNotBlank(sortBy)) {
            String orderByClause = sortBy + (desc ? "DESC" : "ASC");
            example.setOrderByClause(orderByClause);
        }
        //查询
        List<Brand> list = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            throw new GlobalException(CodeMsg.BRAND_NOT_EXIST);
        }
        //解析分页结果
        PageInfo<Brand> info = new PageInfo<>(list);
        return new PageResult<>(info.getTotal(), list);
    }

    @Override
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌
        brand.setId(null);
        int count = brandMapper.insert(brand);
        if(count != 1) {
            throw new GlobalException(CodeMsg.BRAND_SAVE_ERROR);
        }
        //新增中间表
        for (Long cid : cids){
           count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count != 1) {
                throw new GlobalException(CodeMsg.CATEGORY_BRAND_SAVE_FAILED);
            }
        }
    }

    @Override
    public Brand queryById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (brand == null) {
            throw new GlobalException(CodeMsg.BRAND_NOT_EXIST);
        }
        return brand;
    }

    @Override
    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> list = brandMapper.queryByCategoryId(cid);
        if (CollectionUtils.isEmpty(list)) {
            throw new GlobalException(CodeMsg.BRAND_NOT_EXIST);
        }
        return list;
    }

    @Override
    public List<Brand> queryBrandByIds(List<Long> ids) {
        List<Brand> brands = brandMapper.selectByIdList(ids);

        if (CollectionUtils.isEmpty(brands)) {
            throw new GlobalException(CodeMsg.BRAND_NOT_EXIST);
        }
        return brands;
    }
}
