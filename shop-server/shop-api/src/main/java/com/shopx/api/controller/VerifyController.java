package com.shopx.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopx.common.result.Result;
import com.shopx.dao.mapper.StoreMapper;
import com.shopx.dao.mapper.VerificationCodeMapper;
import com.shopx.model.entity.Store;
import com.shopx.model.entity.VerificationCode;
import com.shopx.model.enums.VerifyStatus;
import com.shopx.model.vo.VerifyCodeVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verify")
public class VerifyController {

    private final VerificationCodeMapper verificationCodeMapper;
    private final StoreMapper storeMapper;

    public VerifyController(VerificationCodeMapper verificationCodeMapper, StoreMapper storeMapper) {
        this.verificationCodeMapper = verificationCodeMapper;
        this.storeMapper = storeMapper;
    }

    @GetMapping("/code/{orderId}")
    public Result<VerifyCodeVO> code(@PathVariable Long orderId) {
        LambdaQueryWrapper<VerificationCode> queryWrapper = new LambdaQueryWrapper<VerificationCode>()
                .eq(VerificationCode::getOrderId, orderId);
        VerificationCode verificationCode = verificationCodeMapper.selectOne(queryWrapper);
        VerifyCodeVO vo = convertToVO(verificationCode);
        return Result.success(vo);
    }

    @GetMapping("/qrcode/{orderId}")
    public Result<VerifyCodeVO> qrcode(@PathVariable Long orderId) {
        LambdaQueryWrapper<VerificationCode> queryWrapper = new LambdaQueryWrapper<VerificationCode>()
                .eq(VerificationCode::getOrderId, orderId);
        VerificationCode verificationCode = verificationCodeMapper.selectOne(queryWrapper);
        VerifyCodeVO vo = convertToVO(verificationCode);
        return Result.success(vo);
    }

    private VerifyCodeVO convertToVO(VerificationCode code) {
        if (code == null) {
            return null;
        }
        VerifyCodeVO vo = new VerifyCodeVO();
        vo.setCode(code.getCode());
        vo.setExpireTime(code.getExpireTime());
        vo.setStatus(code.getStatus());
        if (code.getStatus() != null) {
            VerifyStatus vs = VerifyStatus.fromCode(code.getStatus());
            if (vs != null) {
                vo.setStatusDesc(vs.getDesc());
            }
        }
        if (code.getStoreId() != null) {
            Store store = storeMapper.selectById(code.getStoreId());
            if (store != null) {
                vo.setStoreName(store.getStoreName());
                vo.setStoreAddress(store.getAddress());
            }
        }
        return vo;
    }
}
