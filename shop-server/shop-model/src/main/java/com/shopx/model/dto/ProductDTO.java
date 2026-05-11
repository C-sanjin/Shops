package com.shopx.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {

    private String productName;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private String description;

    private String images;

    private Integer storeScope;

    private List<Long> storeIds;
}
