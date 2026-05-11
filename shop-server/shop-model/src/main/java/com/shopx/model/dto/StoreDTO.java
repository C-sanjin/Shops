package com.shopx.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StoreDTO {

    private String storeName;

    private String storeCode;

    private String address;

    private String contactPhone;

    private String businessHours;

    private BigDecimal longitude;

    private BigDecimal latitude;
}
