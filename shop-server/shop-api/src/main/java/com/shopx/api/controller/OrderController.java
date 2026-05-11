package com.shopx.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopx.common.result.Result;
import com.shopx.model.dto.OrderCreateDTO;
import com.shopx.model.vo.OrderVO;
import com.shopx.service.order.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public Result<OrderVO> create(@RequestBody @Validated OrderCreateDTO dto) {
        Long userId = getCurrentUserId();
        OrderVO orderVO = orderService.createOrder(userId, dto);
        return Result.success(orderVO);
    }

    @GetMapping("/list")
    public Result<IPage<OrderVO>> list(@RequestParam(required = false) Integer orderStatus,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId();
        IPage<OrderVO> result = orderService.getOrderList(userId, orderStatus, page, size);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        OrderVO orderVO = orderService.getOrderDetail(userId, id);
        return Result.success(orderVO);
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        orderService.cancelOrder(userId, id);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
