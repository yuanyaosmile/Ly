package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "tb_spu")
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;
    private Long cid1; //一级类目
    private Long cid2;//二级类目
    private Long cid3;//三级类目

    private String title;//标题
    private String subTitle; //子标题
    private Boolean saleable; //是否上架
    private Boolean valid; //是否有效，逻辑是否删除
    private Date createTime;//创建时间

    @JsonIgnore // 返回对象时忽略 jackson
    private Date lastUpdateTime;//最后修改时间

    @Transient //存储对象时，不需要持久化当前属性
    private String bname;
    @Transient
    private String cname;
    @Transient
    private List<Sku> skus;
    @Transient
    private SpuDetail spuDetail;
}
