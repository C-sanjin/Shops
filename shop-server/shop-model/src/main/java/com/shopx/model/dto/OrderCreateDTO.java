package com.shopx.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateDTO {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量不能小于1")
    private Integer quantity;

    @NotNull(message = "门店ID不能为空")
    private Long storeId;

    @NotNull(message = "支付类型不能为空")
    private Integer payType;

    private Long proxyStoreId;

    private String remark;
}
