package com.yy.item.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import java.util.List;

public interface SpecificationService {
    List<SpecGroup> queryGroupByCid(Long cid);

    List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching);
}
