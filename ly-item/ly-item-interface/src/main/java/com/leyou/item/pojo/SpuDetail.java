package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_detail")
public class SpuDetail {
    @Id
    private Long SpuId; //对应Spu的ID；
    private String description;//商品描述
    private String specialSpec;//善品特殊规格的名称及可选值模板
    private String genericSpec;//商品的全局规格属性
    private String packagingList;//包装清单
    private String afterService;//售后服务

}