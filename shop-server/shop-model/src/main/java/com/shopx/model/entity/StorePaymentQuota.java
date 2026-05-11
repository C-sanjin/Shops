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

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"totalQuota", "usedQuota"})
@TableName("t_store_payment_quota")
public class StorePaymentQuota extends BaseEntity {

    @TableField("store_id")
    private Long storeId;

    @TableField("total_quota")
    private BigDecimal totalQuota;

    @TableField("used_quota")
    private BigDecimal usedQuota;

    @TableField("status")
    private Integer status;

    @Version
    @TableField("version")
    private Integer version;
}
