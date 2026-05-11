package com.shopx.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopx.common.result.Result;
import com.shopx.model.vo.StoreVO;
import com.shopx.service.store.StoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/list")
    public Result<IPage<StoreVO>> list(@RequestParam(required = false) Integer status,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        IPage<StoreVO> result = storeService.listStores(status, page, size);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<StoreVO> detail(@PathVariable Long id) {
        StoreVO storeVO = storeService.getStoreDetail(id);
        return Result.success(storeVO);
    }
}
