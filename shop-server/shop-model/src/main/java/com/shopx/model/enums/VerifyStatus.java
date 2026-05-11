package com.shopx.model.enums;

import lombok.Getter;

@Getter
public enum VerifyStatus {

    UNUSED(0, "未使用"),
    USED(1, "已使用"),
    EXPIRED(2, "已过期"),
    VOIDED(3, "已作废");

    private final int code;
    private final String desc;

    VerifyStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static VerifyStatus fromCode(int code) {
        for (VerifyStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
