package com.yy.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.yy.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    SpecificationService specificationService;

    /**
     * 根据分类ID查询规格组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }

    /**
     * 查询参数集合
     * @param gid
     * @param cid
     * @param searing
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamList(@RequestParam(value = "gid" , required = false)Long gid,
                                                          @RequestParam(value = "cid",required = false)Long cid,
                                                          @RequestParam("searching") Boolean searching){
        return ResponseEntity.ok(specificationService.queryParamList(gid, cid, searching));
    }


}
