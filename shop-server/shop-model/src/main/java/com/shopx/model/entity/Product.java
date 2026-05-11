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
@EqualsAndHashCode(callSuper = true, exclude = {"price", "originalPrice"})
@TableName("t_product")
public class Product extends BaseEntity {

    @TableField("product_name")
    private String productName;

    @TableField("price")
    private BigDecimal price;

    @TableField("original_price")
    private BigDecimal originalPrice;

    @TableField("stock")
    private Integer stock;

    @TableField("description")
    private String description;

    @TableField("images")
    private String images;

    @TableField("store_scope")
    private Integer storeScope;

    @TableField("status")
    private Integer status;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
