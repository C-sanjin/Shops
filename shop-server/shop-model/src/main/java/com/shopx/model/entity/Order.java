package com.shopx.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shopx.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"totalAmount", "payAmount"})
@TableName("t_order")
public class Order extends BaseEntity {

    @TableField("order_no")
    private String orderNo;

    @TableField("user_id")
    private Long userId;

    @TableField("store_id")
    private Long storeId;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("pay_amount")
    private BigDecimal payAmount;

    @TableField("pay_type")
    private Integer payType;

    @TableField("pay_status")
    private Integer payStatus;

    @TableField("order_status")
    private Integer orderStatus;

    @TableField("proxy_store_id")
    private Long proxyStoreId;

    @TableField("remark")
    private String remark;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField("pay_time")
    private LocalDateTime payTime;
}
