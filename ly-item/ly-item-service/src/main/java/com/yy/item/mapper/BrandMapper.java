package com.yy.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface BrandMapper extends Mapper<Brand>, IdListMapper {

    @Insert("INSERT INTO ta_category_brand (category_id, brand_id) VALUES (#{cid}), #{bid}")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Select("SELECT b.* FROM tb_category_brand cb INNER JOIN tb_brand b ON b.id = cb.id WHERE cb.category_id = #{cid}")
    List<Brand> queryByCategoryId(@Param("cid") Long cid);
}
