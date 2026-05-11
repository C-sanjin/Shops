package com.shopx.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StoreVO {

    private Long id;

    private String storeName;

    private String storeCode;

    private String address;

    private String contactPhone;

    private String businessHours;

    private Integer status;

    private BigDecimal longitude;

    private BigDecimal latitude;
}
