package com.yy.item.service.impl;

import com.leyou.item.pojo.Category;
import com.ly.common.exception.GlobalException;
import com.ly.common.vo.CodeMsg;
import com.yy.item.mapper.CategoryMapper;
import com.yy.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;


    @Override
    public List<Category> queryCategoryListByPid(Long pid) {
        Category t = new Category();
        t.setParentId(pid);
        List<Category> list = categoryMapper.select(t);

        if(CollectionUtils.isEmpty(list)){
            throw new GlobalException(CodeMsg.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    @Override
    public List<Category> queryByIds(List<Long> ids) {
        List<Category> list = categoryMapper.selectByIdList(ids);

        if(CollectionUtils.isEmpty(list)){
            throw new GlobalException(CodeMsg.CATEGORY_NOT_FOUND);
        }
        return list;
    }
}
