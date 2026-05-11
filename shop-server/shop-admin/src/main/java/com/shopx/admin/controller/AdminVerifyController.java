package com.shopx.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopx.common.result.Result;
import com.shopx.model.dto.VerifyDTO;
import com.shopx.model.entity.VerificationCode;
import com.shopx.model.vo.VerifyCodeVO;
import com.shopx.service.verification.VerificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/admin/verify")
public class AdminVerifyController {

    private final VerificationService verificationService;

    public AdminVerifyController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/batchGenerate")
    public Result<Void> batchGenerate(@RequestBody Map<String, Object> params) {
        Long storeId = Long.valueOf(params.get("storeId").toString());
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        int quantity = Integer.parseInt(params.get("quantity").toString());
        Long operatorId = getCurrentUserId();
        verificationService.batchGenerate(storeId, amount, quantity, operatorId);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<IPage<VerificationCode>> list(@RequestParam(required = false) String batchNo,
                                                 @RequestParam(required = false) Integer status,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        IPage<VerificationCode> result = verificationService.listCodes(batchNo, status, page, size);
        return Result.success(result);
    }

    @PostMapping("/scan")
    public Result<VerifyCodeVO> scan(@RequestBody VerifyDTO dto) {
        Long operatorId = getCurrentUserId();
        VerifyCodeVO result = verificationService.verifyByScan(dto.getCode(), dto.getStoreId(), operatorId);
        return Result.success(result);
    }

    @PostMapping("/manual")
    public Result<VerifyCodeVO> manual(@RequestBody VerifyDTO dto) {
        Long operatorId = getCurrentUserId();
        VerifyCodeVO result = verificationService.verifyManual(dto.getCode(), dto.getStoreId(), operatorId);
        return Result.success(result);
    }

    @PostMapping("/void/{code}")
    public Result<Void> voidCode(@PathVariable String code) {
        Long operatorId = getCurrentUserId();
        verificationService.voidCode(code, operatorId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
