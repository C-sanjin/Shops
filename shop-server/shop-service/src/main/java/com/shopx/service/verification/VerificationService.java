package com.shopx.service.verification;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopx.model.entity.Order;
import com.shopx.model.entity.VerificationCode;
import com.shopx.model.vo.VerifyCodeVO;

import java.math.BigDecimal;

public interface VerificationService {
    VerifyCodeVO generateCode(Order order);
    VerifyCodeVO verifyByScan(String code, Long storeId, Long operatorId);
    VerifyCodeVO verifyManual(String code, Long storeId, Long operatorId);
    void batchGenerate(Long storeId, BigDecimal amount, int quantity, Long operatorId);
    IPage<VerificationCode> listCodes(String batchNo, Integer status, int page, int size);
    void voidCode(String code, Long operatorId);
}
