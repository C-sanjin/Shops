package com.shopx.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shopx.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"longitude", "latitude"})
@TableName("t_store")
public class Store extends BaseEntity {

    @TableField("store_name")
    private String storeName;

    @TableField("store_code")
    private String storeCode;

    @TableField("address")
    private String address;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("business_hours")
    private String businessHours;

    @TableField("status")
    private Integer status;

    @TableField("longitude")
    private BigDecimal longitude;

    @TableField("latitude")
    private BigDecimal latitude;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
