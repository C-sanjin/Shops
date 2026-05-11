package com.shopx.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {

    private String orderNo;

    private Long userId;

    private Long storeId;

    private String storeName;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private Integer payType;

    private String payTypeDesc;

    private Integer payStatus;

    private String payStatusDesc;

    private Integer orderStatus;

    private String orderStatusDesc;

    private Long proxyStoreId;

    private String remark;

    private LocalDateTime expireTime;

    private LocalDateTime payTime;

    private LocalDateTime createdAt;

    private List<OrderItemVO> items;

    private VerifyCodeVO verifyCode;
}
