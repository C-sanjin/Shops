package com.shopx.model.enums;

public enum PayType {

    WECHAT(1, "微信支付"),
    ALIPAY(2, "支付宝"),
    VIRTUAL(3, "虚拟支付"),
    STORE_PROXY(4, "门店代付");

    private final int code;
    private final String desc;

    PayType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PayType fromCode(int code) {
        for (PayType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
