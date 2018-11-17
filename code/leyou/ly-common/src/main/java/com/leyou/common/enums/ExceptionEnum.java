package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.omg.CORBA.INVALID_ACTIVITY;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: cuzz
 * @Date: 2018/10/31 19:20
 * @Description: 异常的枚举
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    PRICE_CANNOT_BE_NULL(400, "价格不能为空"),
    CATEGORY_NOT_FOUND(404, "商品分类没有找到"),
    SPEC_GROUP_NOT_FOUND(404, "商品规格组没有查到"),
    BRAND_SAVE_ERROR(500, "新增品牌失败"),
    GOODS_SAVE_ERROR(500, "新增商品失败"),
    BRAND_NOT_FOUND(404, "品牌没有找到"),
    UPLOAD_FILE_ERROR(500, "文件上传失败"),
    INVALID_FILE_TYPE(400, "无效文件类型"),
    SPEC_PARAM_NOT_FOUND(404,"商品规格参数不存在"),
    GOODS_NOT_FOUND(404,"商品不存在"),
    SPU_DETAIL_NOT_FOUND(404, "商品详情不存在"),
    GOODS_SKU_NOT_FOUND(404,"sku没有找到"),
    INVALID_USER_DATA_TYPE(400, "请求参数有误"),
    INVALID_CODE(400, "短息验证码有误"),
    INVALID_USERNAME_OR_PASSWORD(400, "无效用户名或密码"),
    GENERATE_TOKEN_ERROR(500, "生成token失败"),
    UNAUTHORIZED(403, "未授权"),
    ;


    private int code;
    private String msg;
}
