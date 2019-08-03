package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "tb_spec_group")
public class SpecGroup {

    private Long id;
    private Long cid;
    private String name;
}
