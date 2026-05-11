package com.shopx.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVO {

    private Long id;

    private String productName;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private String description;

    private String images;

    private Integer storeScope;

    private Integer status;

    private List<StoreVO> stores;
}
