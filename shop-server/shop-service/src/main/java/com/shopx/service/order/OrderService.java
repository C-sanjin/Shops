package com.shopx.service.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopx.model.dto.OrderCreateDTO;
import com.shopx.model.entity.Order;
import com.shopx.model.enums.OrderStatus;
import com.shopx.model.vo.OrderVO;

public interface OrderService {
    OrderVO createOrder(Long userId, OrderCreateDTO dto);
    IPage<OrderVO> getOrderList(Long userId, Integer orderStatus, int page, int size);
    OrderVO getOrderDetail(Long userId, Long orderId);
    void cancelOrder(Long userId, Long orderId);
    void updateOrderStatus(Long orderId, OrderStatus status);
}
