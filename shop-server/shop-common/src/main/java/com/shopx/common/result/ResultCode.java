package com.shopx.common.result;

public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(400, "参数错误"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_PHONE_EXISTS(1003, "手机号已注册"),

    STORE_NOT_FOUND(2001, "门店不存在"),
    STORE_DISABLED(2002, "门店已禁用"),

    PRODUCT_NOT_FOUND(3001, "商品不存在"),
    PRODUCT_OFF_SHELF(3002, "商品已下架"),
    PRODUCT_STOCK_INSUFFICIENT(3003, "库存不足"),

    ORDER_NOT_FOUND(4001, "订单不存在"),
    ORDER_STATUS_ERROR(4002, "订单状态异常"),

    PAY_AMOUNT_ERROR(5001, "支付金额异常"),
    VIRTUAL_CONFIG_NOT_FOUND(5002, "虚拟支付配置不存在"),
    VIRTUAL_CONFIG_DISABLED(5003, "虚拟支付配置已禁用"),
    VIRTUAL_AMOUNT_INSUFFICIENT(5004, "虚拟支付额度不足"),
    STORE_QUOTA_INSUFFICIENT(5005, "门店代付额度不足"),

    VERIFY_CODE_NOT_FOUND(6001, "核销码不存在"),
    VERIFY_CODE_USED(6002, "核销码已使用"),
    VERIFY_CODE_EXPIRED(6003, "核销码已过期"),
    VERIFY_CODE_STORE_MISMATCH(6004, "核销码需在指定门店使用"),
    VERIFY_LOCK_FAILED(6005, "核销操作中请稍后"),
    VERIFY_CODE_VOIDED(6006, "核销码已作废");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
