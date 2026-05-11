package com.shopx.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.shopx.common.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Objects;

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

    public StorePaymentQuota() {
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public BigDecimal getTotalQuota() {
        return totalQuota;
    }

    public void setTotalQuota(BigDecimal totalQuota) {
        this.totalQuota = totalQuota;
    }

    public BigDecimal getUsedQuota() {
        return usedQuota;
    }

    public void setUsedQuota(BigDecimal usedQuota) {
        this.usedQuota = usedQuota;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorePaymentQuota that = (StorePaymentQuota) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
