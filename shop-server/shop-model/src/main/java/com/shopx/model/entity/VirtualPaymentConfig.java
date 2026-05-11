package com.shopx.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
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
@EqualsAndHashCode(callSuper = true, exclude = {"totalAmount", "usedAmount"})
@TableName("t_virtual_payment_config")
public class VirtualPaymentConfig extends BaseEntity {

    @TableField("config_name")
    private String configName;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("used_amount")
    private BigDecimal usedAmount;

    @TableField("store_id")
    private Long storeId;

    @TableField("status")
    private Integer status;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @Version
    @TableField("version")
    private Integer version;
}
