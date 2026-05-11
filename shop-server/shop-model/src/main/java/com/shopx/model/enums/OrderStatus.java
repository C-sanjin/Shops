package com.shopx.model.enums;

public enum OrderStatus {

    PENDING(0, "待支付"),
    AWAITING_VERIFY(1, "待核销"),
    VERIFIED(2, "已核销"),
    CANCELLED(3, "已取消"),
    EXPIRED(4, "已过期");

    private final int code;
    private final String desc;

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
