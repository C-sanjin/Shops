package com.shopx.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopx.common.result.Result;
import com.shopx.model.dto.ProductDTO;
import com.shopx.model.vo.ProductVO;
import com.shopx.service.product.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/product")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/list")
    public Result<IPage<ProductVO>> list(@RequestParam(required = false) Integer status,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        IPage<ProductVO> result = productService.listProducts(status, page, size);
        return Result.success(result);
    }

    @PostMapping("/create")
    public Result<Void> create(@RequestBody ProductDTO dto) {
        productService.createProduct(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProductDTO dto) {
        productService.updateProduct(id, dto);
        return Result.success();
    }

    @PostMapping("/bindStore")
    public Result<Void> bindStore(@RequestBody Map<String, Object> params) {
        Long productId = Long.valueOf(params.get("productId").toString());
        @SuppressWarnings("unchecked")
        List<Long> storeIds = ((List<Number>) params.get("storeIds")).stream()
                .map(Number::longValue)
                .toList();
        productService.bindStore(productId, storeIds);
        return Result.success();
    }

    @PostMapping("/unbindStore")
    public Result<Void> unbindStore(@RequestBody Map<String, Object> params) {
        Long productId = Long.valueOf(params.get("productId").toString());
        @SuppressWarnings("unchecked")
        List<Long> storeIds = ((List<Number>) params.get("storeIds")).stream()
                .map(Number::longValue)
                .toList();
        productService.unbindStore(productId, storeIds);
        return Result.success();
    }
}
