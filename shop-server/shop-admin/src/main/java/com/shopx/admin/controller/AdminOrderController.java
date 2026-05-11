package com.shopx.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopx.common.result.Result;
import com.shopx.model.vo.OrderVO;
import com.shopx.service.order.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/list")
    public Result<IPage<OrderVO>> list(@RequestParam(required = false) Integer orderStatus,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        IPage<OrderVO> result = orderService.getOrderList(null, orderStatus, page, size);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderDetail(null, id);
        return Result.success(orderVO);
    }

    @PostMapping("/{id}/refund")
    public Result<Void> refund(@PathVariable Long id) {
        return Result.success();
    }
}
