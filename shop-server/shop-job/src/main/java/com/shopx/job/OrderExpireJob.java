package com.shopx.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.shopx.dao.mapper.OperationLogMapper;
import com.shopx.dao.mapper.OrderMapper;
import com.shopx.dao.mapper.StorePaymentQuotaMapper;
import com.shopx.dao.mapper.VerificationCodeMapper;
import com.shopx.dao.mapper.VirtualPaymentConfigMapper;
import com.shopx.dao.redis.RedisService;
import com.shopx.model.entity.OperationLog;
import com.shopx.model.entity.Order;
import com.shopx.model.entity.StorePaymentQuota;
import com.shopx.model.entity.VerificationCode;
import com.shopx.model.entity.VirtualPaymentConfig;
import com.shopx.model.enums.OrderStatus;
import com.shopx.model.enums.PayType;
import com.shopx.model.enums.VerifyStatus;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class OrderExpireJob implements Job {

    private final OrderMapper orderMapper;
    private final VerificationCodeMapper verificationCodeMapper;
    private final VirtualPaymentConfigMapper virtualPaymentConfigMapper;
    private final StorePaymentQuotaMapper storePaymentQuotaMapper;
    private final OperationLogMapper operationLogMapper;
    private final RedisService redisService;

    public OrderExpireJob(OrderMapper orderMapper,
                          VerificationCodeMapper verificationCodeMapper,
                          VirtualPaymentConfigMapper virtualPaymentConfigMapper,
                          StorePaymentQuotaMapper storePaymentQuotaMapper,
                          OperationLogMapper operationLogMapper,
                          RedisService redisService) {
        this.orderMapper = orderMapper;
        this.verificationCodeMapper = verificationCodeMapper;
        this.virtualPaymentConfigMapper = virtualPaymentConfigMapper;
        this.storePaymentQuotaMapper = storePaymentQuotaMapper;
        this.operationLogMapper = operationLogMapper;
        this.redisService = redisService;
    }

    @Override
    public void execute(JobExecutionContext context) {
        log.info("OrderExpireJob start: checking expired orders");
        List<Order> expiredOrders = orderMapper.selectExpiredAwaitingOrders();
        if (expiredOrders == null || expiredOrders.isEmpty()) {
            log.info("OrderExpireJob: no expired orders found");
            return;
        }
        log.info("OrderExpireJob: found {} expired orders", expiredOrders.size());
        for (Order order : expiredOrders) {
            try {
                processExpiredOrder(order);
            } catch (Exception e) {
                log.error("OrderExpireJob: failed to process order id={}, error={}", order.getId(), e.getMessage(), e);
            }
        }
        log.info("OrderExpireJob end");
    }

    private void processExpiredOrder(Order order) {
        orderMapper.update(null, new LambdaUpdateWrapper<Order>()
                .eq(Order::getId, order.getId())
                .set(Order::getOrderStatus, OrderStatus.EXPIRED.getCode()));

        verificationCodeMapper.update(null, new LambdaUpdateWrapper<VerificationCode>()
                .eq(VerificationCode::getOrderId, order.getId())
                .eq(VerificationCode::getStatus, VerifyStatus.UNUSED.getCode())
                .set(VerificationCode::getStatus, VerifyStatus.EXPIRED.getCode()));

        PayType payType = PayType.fromCode(order.getPayType());
        if (payType == PayType.VIRTUAL) {
            rollbackVirtualPayment(order);
        } else if (payType == PayType.STORE_PROXY) {
            rollbackStoreProxyQuota(order);
        }

        cleanRedisCache(order);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperatorId(0L);
        operationLog.setOperatorType(2);
        operationLog.setModule("ORDER_EXPIRE");
        operationLog.setAction("EXPIRE");
        operationLog.setDetail("订单过期自动处理，订单号：" + order.getOrderNo());
        operationLogMapper.insert(operationLog);
    }

    private void rollbackVirtualPayment(Order order) {
        LambdaQueryWrapper<VirtualPaymentConfig> queryWrapper = new LambdaQueryWrapper<VirtualPaymentConfig>()
                .eq(VirtualPaymentConfig::getStoreId, order.getStoreId())
                .eq(VirtualPaymentConfig::getStatus, 1);
        VirtualPaymentConfig config = virtualPaymentConfigMapper.selectOne(queryWrapper);
        if (config != null) {
            config.setUsedAmount(config.getUsedAmount().subtract(order.getPayAmount()));
            config.setVersion(config.getVersion() + 1);
            virtualPaymentConfigMapper.updateById(config);
        }
    }

    private void rollbackStoreProxyQuota(Order order) {
        Long proxyStoreId = order.getProxyStoreId();
        if (proxyStoreId == null) {
            return;
        }
        LambdaQueryWrapper<StorePaymentQuota> queryWrapper = new LambdaQueryWrapper<StorePaymentQuota>()
                .eq(StorePaymentQuota::getStoreId, proxyStoreId);
        StorePaymentQuota quota = storePaymentQuotaMapper.selectOne(queryWrapper);
        if (quota != null) {
            quota.setUsedQuota(quota.getUsedQuota().subtract(order.getPayAmount()));
            quota.setVersion(quota.getVersion() + 1);
            storePaymentQuotaMapper.updateById(quota);
        }
    }

    private void cleanRedisCache(Order order) {
        redisService.delete("order:" + order.getId());
        redisService.delete("order:no:" + order.getOrderNo());
        redisService.delete("verify:order:" + order.getId());
    }
}
