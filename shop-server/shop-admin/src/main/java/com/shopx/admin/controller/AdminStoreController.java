package com.shopx.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopx.common.result.Result;
import com.shopx.model.dto.StoreDTO;
import com.shopx.model.entity.StorePaymentQuota;
import com.shopx.model.vo.StoreVO;
import com.shopx.service.store.StoreService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/admin/store")
public class AdminStoreController {

    private final StoreService storeService;

    public AdminStoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/list")
    public Result<IPage<StoreVO>> list(@RequestParam(required = false) Integer status,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        IPage<StoreVO> result = storeService.listStores(status, page, size);
        return Result.success(result);
    }

    @PostMapping("/create")
    public Result<Void> create(@RequestBody StoreDTO dto) {
        storeService.createStore(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StoreDTO dto) {
        storeService.updateStore(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        storeService.deleteStore(id);
        return Result.success();
    }

    @GetMapping("/quota/{storeId}")
    public Result<StorePaymentQuota> getQuota(@PathVariable Long storeId) {
        StorePaymentQuota quota = storeService.getStoreQuota(storeId);
        return Result.success(quota);
    }

    @PostMapping("/quota/{storeId}")
    public Result<Void> setQuota(@PathVariable Long storeId, @RequestBody Map<String, BigDecimal> params) {
        BigDecimal totalQuota = params.get("totalQuota");
        storeService.setStoreQuota(storeId, totalQuota);
        return Result.success();
    }
}
