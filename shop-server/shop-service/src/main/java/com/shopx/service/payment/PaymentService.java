package com.shopx.service.payment;

import com.shopx.model.dto.PaymentDTO;
import com.shopx.model.entity.Order;
import com.shopx.model.vo.VerifyCodeVO;

public interface PaymentService {
    VerifyCodeVO pay(Long userId, PaymentDTO dto);
    VerifyCodeVO virtualPay(Long userId, Order order);
    VerifyCodeVO storeProxyPay(Long userId, Order order);
}
