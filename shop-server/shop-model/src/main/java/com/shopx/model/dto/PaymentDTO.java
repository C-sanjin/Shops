package com.shopx.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentDTO {

    @NotNull(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "支付类型不能为空")
    private Integer payType;

    private Long storeId;
}
