package com.yy.item.service.impl;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.ly.common.exception.GlobalException;
import com.ly.common.vo.CodeMsg;
import com.yy.item.mapper.SpecGroupMapper;
import com.yy.item.mapper.SpecParamMapper;
import com.yy.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    SpecGroupMapper specGroupMapper;

    @Autowired
    SpecParamMapper specParamMapper;

    @Override
    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(group);

        if (CollectionUtils.isEmpty(list)) {
            throw new GlobalException(CodeMsg.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    @Override
    public List<SpecParam> queryParamList(Long gid,Long cid, Boolean searching) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setSearching(searching);

        List<SpecParam> list = specParamMapper.select(param);
        if (CollectionUtils.isEmpty(list)) {
            throw new GlobalException(CodeMsg.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }
}
