package com.ly.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodeMsg {
    private CodeMsg(){
    }

    private int code;
    private String msg;


    public static final CodeMsg USER_NOT_EXIST =  new CodeMsg(10000,"用户不存在");
    public static final CodeMsg BRAND_NOT_EXIST =  new CodeMsg(10001,"品牌不存在");
    public static final CodeMsg BRAND_SAVE_ERROR = new CodeMsg(10002, "新增品牌失败");
    public static final CodeMsg CATEGORY_BRAND_SAVE_FAILED = new CodeMsg(10003, "品牌分类关联失败");
    public static final CodeMsg UPLOAD_IMAGE_FAILED = new CodeMsg(10004,"图片上传失败");
    public static final CodeMsg IMAGE_TYPE_ERROR = new CodeMsg(10005, "图片类型不匹配");
    public static final CodeMsg SPEC_GROUP_NOT_FOUND = new CodeMsg(10006, "商品分组未查询到");
    public static final CodeMsg SPEC_PARAM_NOT_FOUND = new CodeMsg(10007, "商品参数未查询到");
    public static final CodeMsg GOODS_NOT_FOUND = new CodeMsg(10008, "商品未查询到");
    public static final CodeMsg CATEGORY_NOT_FOUND = new CodeMsg(10009, "分类未查询到");
    public static final CodeMsg SAVE_GOODS_FAILED = new CodeMsg(10010, "新增商品失败");
    public static final CodeMsg SPU_DETAIL_NOT_FOUND = new CodeMsg(10011, "未查询到SPU细节");

}
