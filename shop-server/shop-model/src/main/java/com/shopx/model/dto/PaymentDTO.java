package com.shopx.model.dto;

import jakarta.validation.constraints.NotNull;

public class PaymentDTO {

    @NotNull(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "支付类型不能为空")
    private Integer payType;

    private Long storeId;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
}
