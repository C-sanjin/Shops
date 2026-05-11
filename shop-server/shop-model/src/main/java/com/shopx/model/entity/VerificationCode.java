package com.shopx.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.shopx.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_verification_code")
public class VerificationCode extends BaseEntity {

    @TableField("code")
    private String code;

    @TableField("order_id")
    private Long orderId;

    @TableField("order_no")
    private String orderNo;

    @TableField("user_id")
    private Long userId;

    @TableField("store_id")
    private Long storeId;

    @TableField("status")
    private Integer status;

    @TableField("used_time")
    private LocalDateTime usedTime;

    @TableField("verified_by")
    private String verifiedBy;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField("batch_no")
    private String batchNo;

    @Version
    @TableField("version")
    private Integer version;
}
