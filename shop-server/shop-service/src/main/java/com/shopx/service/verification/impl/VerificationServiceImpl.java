package com.shopx.service.verification.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.shopx.common.exception.BizException;
import com.shopx.common.result.ResultCode;
import com.shopx.common.utils.CodeGeneratorUtil;
import com.shopx.common.utils.RedisLockUtil;
import com.shopx.dao.mapper.OperationLogMapper;
import com.shopx.dao.mapper.OrderMapper;
import com.shopx.dao.mapper.StoreMapper;
import com.shopx.dao.mapper.VerificationCodeMapper;
import com.shopx.dao.redis.RedisService;
import com.shopx.model.entity.OperationLog;
import com.shopx.model.entity.Order;
import com.shopx.model.entity.Store;
import com.shopx.model.entity.VerificationCode;
import com.shopx.model.enums.OrderStatus;
import com.shopx.model.enums.VerifyStatus;
import com.shopx.model.vo.VerifyCodeVO;
import com.shopx.service.verification.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final VerificationCodeMapper verificationCodeMapper;
    private final StoreMapper storeMapper;
    private final OrderMapper orderMapper;
    private final OperationLogMapper operationLogMapper;
    private final RedisService redisService;
    private final RedisLockUtil redisLockUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VerifyCodeVO generateCode(Order order) {
        VerificationCode vc = null;
        String code = null;
        for (int i = 0; i < 3; i++) {
            code = CodeGeneratorUtil.generateVerifyCode("VP");
            vc = new VerificationCode();
            vc.setCode(code);
            vc.setOrderId(order.getId());
            vc.setOrderNo(order.getOrderNo());
            vc.setUserId(order.getUserId());
            vc.setStoreId(order.getStoreId());
            vc.setExpireTime(order.getExpireTime());
            vc.setStatus(VerifyStatus.UNUSED.getCode());
            vc.setVersion(0);
            try {
                verificationCodeMapper.insert(vc);
                break;
            } catch (DuplicateKeyException e) {
                log.warn("核销码重复，重试第{}次: {}", i + 1, code);
                if (i == 2) {
                    throw new BizException(ResultCode.FAIL, "核销码生成失败，请重试");
                }
            }
        }

        String redisKey = "verify:code:" + code;
        redisService.hashSet(redisKey, "status", "0");
        redisService.hashSet(redisKey, "storeId", String.valueOf(order.getStoreId()));
        long expireSeconds = Duration.between(LocalDateTime.now(), order.getExpireTime()).getSeconds();
        if (expireSeconds > 0) {
            redisService.expire(redisKey, expireSeconds, TimeUnit.SECONDS);
        }

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(code, BarcodeFormat.QR_CODE, 300, 300);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            String qrcodeUrl = "data:image/png;base64," + base64Image;

            Store store = storeMapper.selectById(order.getStoreId());
            VerifyCodeVO vo = new VerifyCodeVO();
            vo.setCode(code);
            vo.setQrcodeUrl(qrcodeUrl);
            vo.setStoreName(store != null ? store.getStoreName() : null);
            vo.setStoreAddress(store != null ? store.getAddress() : null);
            vo.setExpireTime(order.getExpireTime());
            vo.setStatus(VerifyStatus.UNUSED.getCode());
            vo.setStatusDesc("未使用");
            return vo;
        } catch (Exception e) {
            log.error("生成二维码失败", e);
            throw new BizException(ResultCode.FAIL, "二维码生成失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VerifyCodeVO verifyByScan(String code, Long storeId, Long operatorId) {
        String lockKey = "verify:lock:" + code;
        boolean locked = redisLockUtil.tryLock(lockKey, 30);
        if (!locked) {
            throw new BizException(ResultCode.VERIFY_LOCK_FAILED);
        }
        try {
            Object cachedStatus = redisService.hashGet("verify:code:" + code, "status");
            VerificationCode vc;
            if (cachedStatus != null) {
                vc = verificationCodeMapper.selectByCode(code);
            } else {
                vc = verificationCodeMapper.selectByCode(code);
            }
            if (vc == null) {
                throw new BizException(ResultCode.VERIFY_CODE_NOT_FOUND);
            }
            if (vc.getStatus() == VerifyStatus.USED.getCode()) {
                throw new BizException(ResultCode.VERIFY_CODE_USED);
            }
            if (vc.getStatus() == VerifyStatus.EXPIRED.getCode()) {
                throw new BizException(ResultCode.VERIFY_CODE_EXPIRED);
            }
            if (vc.getStatus() == VerifyStatus.VOIDED.getCode()) {
                throw new BizException(ResultCode.VERIFY_CODE_VOIDED);
            }
            if (!vc.getStoreId().equals(storeId)) {
                throw new BizException(ResultCode.VERIFY_CODE_STORE_MISMATCH);
            }

            LambdaUpdateWrapper<VerificationCode> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(VerificationCode::getCode, code)
                    .eq(VerificationCode::getVersion, vc.getVersion())
                    .eq(VerificationCode::getStatus, VerifyStatus.UNUSED.getCode())
                    .set(VerificationCode::getStatus, VerifyStatus.USED.getCode())
                    .set(VerificationCode::getUsedTime, LocalDateTime.now())
                    .set(VerificationCode::getVerifiedBy, String.valueOf(operatorId))
                    .set(VerificationCode::getVersion, vc.getVersion() + 1);
            int updated = verificationCodeMapper.update(null, updateWrapper);
            if (updated == 0) {
                throw new BizException(ResultCode.VERIFY_CODE_USED);
            }

            Order order = new Order();
            order.setId(vc.getOrderId());
            order.setOrderStatus(OrderStatus.VERIFIED.getCode());
            orderMapper.updateById(order);

            redisService.delete("verify:code:" + code);

            logOperation(operatorId, 1, "verification", "scan_verify", code);

            vc.setStatus(VerifyStatus.USED.getCode());
            return buildVerifyCodeVO(vc);
        } finally {
            redisLockUtil.unlock(lockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VerifyCodeVO verifyManual(String code, Long storeId, Long operatorId) {
        String lockKey = "verify:lock:" + code;
        boolean locked = redisLockUtil.tryLock(lockKey, 30);
        if (!locked) {
            throw new BizException(ResultCode.VERIFY_LOCK_FAILED);
        }
        try {
            Object cachedStatus = redisService.hashGet("verify:code:" + code, "status");
            VerificationCode vc;
            if (cachedStatus != null) {
                vc = verificationCodeMapper.selectByCode(code);
            } else {
                vc = verificationCodeMapper.selectByCode(code);
            }
            if (vc == null) {
                throw new BizException(ResultCode.VERIFY_CODE_NOT_FOUND);
            }
            if (vc.getStatus() == VerifyStatus.USED.getCode()) {
                throw new BizException(ResultCode.VERIFY_CODE_USED);
            }
            if (vc.getStatus() == VerifyStatus.EXPIRED.getCode()) {
                throw new BizException(ResultCode.VERIFY_CODE_EXPIRED);
            }
            if (vc.getStatus() == VerifyStatus.VOIDED.getCode()) {
                throw new BizException(ResultCode.VERIFY_CODE_VOIDED);
            }
            if (!vc.getStoreId().equals(storeId)) {
                throw new BizException(ResultCode.VERIFY_CODE_STORE_MISMATCH);
            }

            LambdaUpdateWrapper<VerificationCode> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(VerificationCode::getCode, code)
                    .eq(VerificationCode::getVersion, vc.getVersion())
                    .eq(VerificationCode::getStatus, VerifyStatus.UNUSED.getCode())
                    .set(VerificationCode::getStatus, VerifyStatus.USED.getCode())
                    .set(VerificationCode::getUsedTime, LocalDateTime.now())
                    .set(VerificationCode::getVerifiedBy, String.valueOf(operatorId))
                    .set(VerificationCode::getVersion, vc.getVersion() + 1);
            int updated = verificationCodeMapper.update(null, updateWrapper);
            if (updated == 0) {
                throw new BizException(ResultCode.VERIFY_CODE_USED);
            }

            Order order = new Order();
            order.setId(vc.getOrderId());
            order.setOrderStatus(OrderStatus.VERIFIED.getCode());
            orderMapper.updateById(order);

            redisService.delete("verify:code:" + code);

            logOperation(operatorId, 1, "verification", "manual_verify", code);

            vc.setStatus(VerifyStatus.USED.getCode());
            return buildVerifyCodeVO(vc);
        } finally {
            redisLockUtil.unlock(lockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchGenerate(Long storeId, BigDecimal amount, int quantity, Long operatorId) {
        String batchNo = CodeGeneratorUtil.generateBatchNo();

        Store store = storeMapper.selectById(storeId);
        if (store == null || store.getDeleted() == 1) {
            throw new BizException(ResultCode.STORE_NOT_FOUND);
        }

        BigDecimal totalAmount = amount.multiply(BigDecimal.valueOf(quantity));

        int batchSize = 1000;
        int remaining = quantity;

        while (remaining > 0) {
            int currentBatchSize = Math.min(remaining, batchSize);

            for (int i = 0; i < currentBatchSize; i++) {
                String code = CodeGeneratorUtil.generateVerifyCode("VP");
                VerificationCode vc = new VerificationCode();
                vc.setCode(code);
                vc.setOrderId(0L);
                vc.setOrderNo(batchNo);
                vc.setUserId(0L);
                vc.setStoreId(storeId);
                vc.setExpireTime(LocalDateTime.now().plusHours(168));
                vc.setBatchNo(batchNo);
                vc.setStatus(VerifyStatus.UNUSED.getCode());
                vc.setVersion(0);
                verificationCodeMapper.insert(vc);

                String redisKey = "verify:code:" + code;
                redisService.hashSet(redisKey, "status", "0");
                redisService.hashSet(redisKey, "storeId", String.valueOf(storeId));
                redisService.expire(redisKey, 168, TimeUnit.HOURS);
            }

            remaining -= currentBatchSize;
        }

        logOperation(operatorId, 1, "verification", "batch_generate",
                "batchNo=" + batchNo + ",quantity=" + quantity + ",totalAmount=" + totalAmount);
    }

    @Override
    public IPage<VerificationCode> listCodes(String batchNo, Integer status, int page, int size) {
        LambdaQueryWrapper<VerificationCode> wrapper = new LambdaQueryWrapper<>();
        if (batchNo != null && !batchNo.isEmpty()) {
            wrapper.eq(VerificationCode::getBatchNo, batchNo);
        }
        if (status != null) {
            wrapper.eq(VerificationCode::getStatus, status);
        }
        wrapper.orderByDesc(VerificationCode::getCreatedAt);

        Page<VerificationCode> codePage = new Page<>(page, size);
        return verificationCodeMapper.selectPage(codePage, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void voidCode(String code, Long operatorId) {
        VerificationCode vc = verificationCodeMapper.selectByCode(code);
        if (vc == null) {
            throw new BizException(ResultCode.VERIFY_CODE_NOT_FOUND);
        }
        if (vc.getStatus() != VerifyStatus.UNUSED.getCode()) {
            throw new BizException(ResultCode.VERIFY_CODE_USED);
        }

        LambdaUpdateWrapper<VerificationCode> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(VerificationCode::getCode, code)
                .eq(VerificationCode::getStatus, VerifyStatus.UNUSED.getCode())
                .set(VerificationCode::getStatus, VerifyStatus.VOIDED.getCode());
        verificationCodeMapper.update(null, updateWrapper);

        redisService.delete("verify:code:" + code);

        logOperation(operatorId, 1, "verification", "void_code", code);
    }

    private VerifyCodeVO buildVerifyCodeVO(VerificationCode vc) {
        VerifyCodeVO vo = new VerifyCodeVO();
        vo.setCode(vc.getCode());
        vo.setExpireTime(vc.getExpireTime());
        vo.setStatus(vc.getStatus());

        VerifyStatus vs = VerifyStatus.fromCode(vc.getStatus());
        if (vs != null) {
            vo.setStatusDesc(vs.getDesc());
        }

        Store store = storeMapper.selectById(vc.getStoreId());
        if (store != null) {
            vo.setStoreName(store.getStoreName());
            vo.setStoreAddress(store.getAddress());
        }

        return vo;
    }

    private void logOperation(Long operatorId, int operatorType, String module, String action, String detail) {
        OperationLog operationLog = new OperationLog();
        operationLog.setOperatorId(operatorId);
        operationLog.setOperatorType(operatorType);
        operationLog.setModule(module);
        operationLog.setAction(action);
        operationLog.setDetail(detail);
        operationLogMapper.insert(operationLog);
    }
}
