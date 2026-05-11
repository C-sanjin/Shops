package com.shopx.api.controller;

import com.shopx.common.result.Result;
import com.shopx.model.dto.PaymentDTO;
import com.shopx.model.vo.VerifyCodeVO;
import com.shopx.service.payment.PaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public Result<VerifyCodeVO> pay(@RequestBody @Validated PaymentDTO dto) {
        Long userId = getCurrentUserId();
        VerifyCodeVO verifyCodeVO = paymentService.pay(userId, dto);
        return Result.success(verifyCodeVO);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
