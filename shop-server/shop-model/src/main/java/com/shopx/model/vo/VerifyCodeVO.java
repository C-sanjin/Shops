package com.shopx.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VerifyCodeVO {

    private String code;

    private String qrcodeUrl;

    private String storeName;

    private String storeAddress;

    private LocalDateTime expireTime;

    private Integer status;

    private String statusDesc;
}
