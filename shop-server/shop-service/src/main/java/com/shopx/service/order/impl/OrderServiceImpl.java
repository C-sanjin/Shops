package com.shopx.service.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopx.common.exception.BizException;
import com.shopx.common.result.ResultCode;
import com.shopx.common.utils.CodeGeneratorUtil;
import com.shopx.dao.mapper.OrderItemMapper;
import com.shopx.dao.mapper.OrderMapper;
import com.shopx.dao.mapper.ProductMapper;
import com.shopx.dao.mapper.StoreMapper;
import com.shopx.dao.mapper.VerificationCodeMapper;
import com.shopx.model.dto.OrderCreateDTO;
import com.shopx.model.entity.Order;
import com.shopx.model.entity.OrderItem;
import com.shopx.model.entity.Product;
import com.shopx.model.entity.Store;
import com.shopx.model.entity.VerificationCode;
import com.shopx.model.enums.OrderStatus;
import com.shopx.model.enums.PayStatus;
import com.shopx.model.enums.PayType;
import com.shopx.model.enums.VerifyStatus;
import com.shopx.model.vo.OrderItemVO;
import com.shopx.model.vo.OrderVO;
import com.shopx.model.vo.VerifyCodeVO;
import com.shopx.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductMapper productMapper;
    private final StoreMapper storeMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final VerificationCodeMapper verificationCodeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(Long userId, OrderCreateDTO dto) {
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null || product.getDeleted() == 1) {
            throw new BizException(ResultCode.PRODUCT_NOT_FOUND);
        }
        if (product.getStatus() != 1) {
            throw new BizException(ResultCode.PRODUCT_OFF_SHELF);
        }
        if (product.getStock() < dto.getQuantity()) {
            throw new BizException(ResultCode.PRODUCT_STOCK_INSUFFICIENT);
        }

        Store store = storeMapper.selectById(dto.getStoreId());
        if (store == null || store.getDeleted() == 1) {
            throw new BizException(ResultCode.STORE_NOT_FOUND);
        }
        if (store.getStatus() != 1) {
            throw new BizException(ResultCode.STORE_DISABLED);
        }

        if (dto.getPayType() != null && dto.getPayType() == PayType.STORE_PROXY.getCode()) {
            if (dto.getProxyStoreId() == null) {
                throw new BizException(ResultCode.PARAM_ERROR, "代付门店ID不能为空");
            }
            Store proxyStore = storeMapper.selectById(dto.getProxyStoreId());
            if (proxyStore == null || proxyStore.getDeleted() == 1) {
                throw new BizException(ResultCode.STORE_NOT_FOUND, "代付门店不存在");
            }
            if (proxyStore.getStatus() != 1) {
                throw new BizException(ResultCode.STORE_DISABLED, "代付门店已禁用");
            }
        }

        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
        BigDecimal payAmount = totalAmount;

        LambdaUpdateWrapper<Product> stockWrapper = new LambdaUpdateWrapper<>();
        stockWrapper.eq(Product::getId, product.getId())
                .ge(Product::getStock, dto.getQuantity())
                .setSql("stock = stock - " + dto.getQuantity());
        int rows = productMapper.update(null, stockWrapper);
        if (rows == 0) {
            throw new BizException(ResultCode.PRODUCT_STOCK_INSUFFICIENT);
        }

        String orderNo = CodeGeneratorUtil.generateOrderNo();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStoreId(dto.getStoreId());
        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setPayType(dto.getPayType());
        order.setPayStatus(PayStatus.UNPAID.getCode());
        order.setOrderStatus(OrderStatus.PENDING.getCode());
        order.setProxyStoreId(dto.getProxyStoreId());
        order.setRemark(dto.getRemark());
        order.setExpireTime(LocalDateTime.now().plusHours(168));
        orderMapper.insert(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getProductName());
        orderItem.setPrice(product.getPrice());
        orderItem.setQuantity(dto.getQuantity());
        orderItemMapper.insert(orderItem);

        return convertToVO(order);
    }

    @Override
    public IPage<OrderVO> getOrderList(Long userId, Integer orderStatus, int page, int size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (orderStatus != null) {
            wrapper.eq(Order::getOrderStatus, orderStatus);
        }
        wrapper.orderByDesc(Order::getCreatedAt);

        Page<Order> orderPage = new Page<>(page, size);
        IPage<Order> result = orderMapper.selectPage(orderPage, wrapper);

        return result.convert(this::convertToVO);
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BizException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(userId)) {
            throw new BizException(ResultCode.ORDER_NOT_FOUND);
        }
        return convertToVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BizException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(userId)) {
            throw new BizException(ResultCode.ORDER_NOT_FOUND);
        }
        if (order.getOrderStatus() != OrderStatus.PENDING.getCode()) {
            throw new BizException(ResultCode.ORDER_STATUS_ERROR);
        }

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : orderItems) {
            LambdaUpdateWrapper<Product> stockWrapper = new LambdaUpdateWrapper<>();
            stockWrapper.eq(Product::getId, item.getProductId())
                    .setSql("stock = stock + " + item.getQuantity());
            productMapper.update(null, stockWrapper);
        }

        order.setOrderStatus(OrderStatus.CANCELLED.getCode());
        orderMapper.updateById(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BizException(ResultCode.ORDER_NOT_FOUND);
        }
        order.setOrderStatus(status.getCode());
        orderMapper.updateById(order);
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        Store store = storeMapper.selectById(order.getStoreId());
        if (store != null) {
            vo.setStoreName(store.getStoreName());
        }

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        List<OrderItemVO> itemVOList = orderItems.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOList);

        LambdaQueryWrapper<VerificationCode> vcWrapper = new LambdaQueryWrapper<>();
        vcWrapper.eq(VerificationCode::getOrderId, order.getId());
        VerificationCode verifyCode = verificationCodeMapper.selectOne(vcWrapper);
        if (verifyCode != null) {
            VerifyCodeVO verifyCodeVO = new VerifyCodeVO();
            verifyCodeVO.setCode(verifyCode.getCode());
            verifyCodeVO.setExpireTime(verifyCode.getExpireTime());
            verifyCodeVO.setStatus(verifyCode.getStatus());
            VerifyStatus vs = VerifyStatus.fromCode(verifyCode.getStatus());
            if (vs != null) {
                verifyCodeVO.setStatusDesc(vs.getDesc());
            }
            Store vcStore = storeMapper.selectById(verifyCode.getStoreId());
            if (vcStore != null) {
                verifyCodeVO.setStoreName(vcStore.getStoreName());
                verifyCodeVO.setStoreAddress(vcStore.getAddress());
            }
            vo.setVerifyCode(verifyCodeVO);
        }

        PayType payType = PayType.fromCode(order.getPayType());
        if (payType != null) {
            vo.setPayTypeDesc(payType.getDesc());
        }
        PayStatus payStatus = PayStatus.fromCode(order.getPayStatus());
        if (payStatus != null) {
            vo.setPayStatusDesc(payStatus.getDesc());
        }
        OrderStatus orderStatus = OrderStatus.fromCode(order.getOrderStatus());
        if (orderStatus != null) {
            vo.setOrderStatusDesc(orderStatus.getDesc());
        }

        return vo;
    }
}
