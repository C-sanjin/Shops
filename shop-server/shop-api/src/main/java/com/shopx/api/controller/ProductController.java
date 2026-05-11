package com.shopx.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopx.common.result.Result;
import com.shopx.model.vo.ProductVO;
import com.shopx.service.product.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/list")
    public Result<IPage<ProductVO>> list(@RequestParam(required = false) Integer status,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        IPage<ProductVO> result = productService.listProducts(status, page, size);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        ProductVO productVO = productService.getProductDetail(id);
        return Result.success(productVO);
    }
}
