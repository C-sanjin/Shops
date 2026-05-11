package com.shopx.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyDTO {

    @NotBlank(message = "核销码不能为空")
    private String code;

    @NotNull(message = "门店ID不能为空")
    private Long storeId;
}
