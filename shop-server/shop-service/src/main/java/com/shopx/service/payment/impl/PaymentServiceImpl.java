package com.shopx.service.payment.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.shopx.common.exception.BizException;
import com.shopx.common.result.ResultCode;
import com.shopx.dao.mapper.OrderMapper;
import com.shopx.dao.mapper.StoreMapper;
import com.shopx.dao.mapper.StorePaymentQuotaMapper;
import com.shopx.dao.mapper.VirtualPaymentConfigMapper;
import com.shopx.model.dto.PaymentDTO;
import com.shopx.model.entity.Order;
import com.shopx.model.entity.StorePaymentQuota;
import com.shopx.model.entity.VirtualPaymentConfig;
import com.shopx.model.enums.OrderStatus;
import com.shopx.model.enums.PayStatus;
import com.shopx.model.enums.PayType;
import com.shopx.model.vo.VerifyCodeVO;
import com.shopx.service.payment.PaymentService;
import com.shopx.service.verification.VerificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final OrderMapper orderMapper;
    private final VirtualPaymentConfigMapper virtualPaymentConfigMapper;
    private final StorePaymentQuotaMapper storePaymentQuotaMapper;
    private final StoreMapper storeMapper;
    private final VerificationService verificationService;

    public PaymentServiceImpl(OrderMapper orderMapper,
                              VirtualPaymentConfigMapper virtualPaymentConfigMapper,
                              StorePaymentQuotaMapper storePaymentQuotaMapper,
                              StoreMapper storeMapper,
                              VerificationService verificationService) {
        this.orderMapper = orderMapper;
        this.virtualPaymentConfigMapper = virtualPaymentConfigMapper;
        this.storePaymentQuotaMapper = storePaymentQuotaMapper;
        this.storeMapper = storeMapper;
        this.verificationService = verificationService;
    }

    @Override
    public VerifyCodeVO pay(Long userId, PaymentDTO dto) {
        Order order = orderMapper.selectByOrderNo(dto.getOrderNo());
        if (!order.getUserId().equals(userId)) {
            throw new BizException(ResultCode.ORDER_STATUS_ERROR);
        }
        if (!Integer.valueOf(OrderStatus.PENDING.getCode()).equals(order.getOrderStatus())) {
            throw new BizException(ResultCode.ORDER_STATUS_ERROR);
        }

        PayType payType = PayType.fromCode(dto.getPayType());
        if (payType == null) {
            throw new BizException(ResultCode.FAIL, "暂不支持该支付方式");
        }
        switch (payType) {
            case VIRTUAL:
                return virtualPay(userId, order);
            case STORE_PROXY:
                return storeProxyPay(userId, order);
            default:
                throw new BizException(ResultCode.FAIL, "暂不支持该支付方式");
        }
    }

    @Override
    public VerifyCodeVO virtualPay(Long userId, Order order) {
        List<VirtualPaymentConfig> configs = virtualPaymentConfigMapper.selectActiveByStoreId(order.getStoreId());
        VirtualPaymentConfig config = (configs != null && !configs.isEmpty()) ? configs.get(0) : null;
        if (config == null) {
            throw new BizException(ResultCode.VIRTUAL_CONFIG_NOT_FOUND);
        }

        LambdaUpdateWrapper<VirtualPaymentConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(VirtualPaymentConfig::getId, config.getId())
                .eq(VirtualPaymentConfig::getVersion, config.getVersion())
                .apply("used_amount + {0} <= total_amount", order.getPayAmount())
                .setSql("used_amount = used_amount + " + order.getPayAmount())
                .set(VirtualPaymentConfig::getVersion, config.getVersion() + 1);
        int rows = virtualPaymentConfigMapper.update(null, updateWrapper);
        if (rows == 0) {
            throw new BizException(ResultCode.VIRTUAL_AMOUNT_INSUFFICIENT);
        }

        VerifyCodeVO verifyCodeVO = verificationService.generateCode(order);

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setPayStatus(PayStatus.PAID.getCode());
        updateOrder.setOrderStatus(OrderStatus.AWAITING_VERIFY.getCode());
        updateOrder.setPayType(PayType.VIRTUAL.getCode());
        updateOrder.setPayTime(LocalDateTime.now());
        orderMapper.updateById(updateOrder);

        return verifyCodeVO;
    }

    @Override
    public VerifyCodeVO storeProxyPay(Long userId, Order order) {
        if (order.getProxyStoreId() == null) {
            throw new BizException(ResultCode.STORE_QUOTA_INSUFFICIENT);
        }

        LambdaUpdateWrapper<StorePaymentQuota> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(StorePaymentQuota::getStoreId, order.getProxyStoreId())
                .eq(StorePaymentQuota::getStatus, 1);
        StorePaymentQuota quota = storePaymentQuotaMapper.selectOne(queryWrapper);
        if (quota == null) {
            throw new BizException(ResultCode.STORE_QUOTA_INSUFFICIENT);
        }

        LambdaUpdateWrapper<StorePaymentQuota> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StorePaymentQuota::getStoreId, quota.getStoreId())
                .eq(StorePaymentQuota::getVersion, quota.getVersion())
                .apply("used_quota + {0} <= total_quota", order.getPayAmount())
                .setSql("used_quota = used_quota + " + order.getPayAmount())
                .set(StorePaymentQuota::getVersion, quota.getVersion() + 1);
        int rows = storePaymentQuotaMapper.update(null, updateWrapper);
        if (rows == 0) {
            throw new BizException(ResultCode.STORE_QUOTA_INSUFFICIENT);
        }

        order.setStoreId(order.getProxyStoreId());
        VerifyCodeVO verifyCodeVO = verificationService.generateCode(order);

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setPayStatus(PayStatus.PAID.getCode());
        updateOrder.setOrderStatus(OrderStatus.AWAITING_VERIFY.getCode());
        updateOrder.setPayType(PayType.STORE_PROXY.getCode());
        updateOrder.setPayTime(LocalDateTime.now());
        updateOrder.setStoreId(order.getProxyStoreId());
        orderMapper.updateById(updateOrder);

        return verifyCodeVO;
    }
}
