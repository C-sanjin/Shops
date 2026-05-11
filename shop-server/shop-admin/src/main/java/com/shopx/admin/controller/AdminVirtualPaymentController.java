package com.shopx.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopx.common.result.Result;
import com.shopx.dao.mapper.VirtualPaymentConfigMapper;
import com.shopx.model.entity.VirtualPaymentConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/virtual")
public class AdminVirtualPaymentController {

    private final VirtualPaymentConfigMapper virtualPaymentConfigMapper;

    public AdminVirtualPaymentController(VirtualPaymentConfigMapper virtualPaymentConfigMapper) {
        this.virtualPaymentConfigMapper = virtualPaymentConfigMapper;
    }

    @GetMapping("/list")
    public Result<IPage<VirtualPaymentConfig>> list(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Page<VirtualPaymentConfig> pageParam = new Page<>(page, size);
        IPage<VirtualPaymentConfig> result = virtualPaymentConfigMapper.selectPage(pageParam,
                new LambdaQueryWrapper<VirtualPaymentConfig>().orderByDesc(VirtualPaymentConfig::getId));
        return Result.success(result);
    }

    @PostMapping("/create")
    public Result<Void> create(@RequestBody VirtualPaymentConfig config) {
        virtualPaymentConfigMapper.insert(config);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody VirtualPaymentConfig config) {
        config.setId(id);
        virtualPaymentConfigMapper.updateById(config);
        return Result.success();
    }

    @PostMapping("/bindStore")
    public Result<Void> bindStore(@RequestBody Map<String, Long> params) {
        Long configId = params.get("configId");
        Long storeId = params.get("storeId");
        VirtualPaymentConfig config = virtualPaymentConfigMapper.selectById(configId);
        if (config != null) {
            config.setStoreId(storeId);
            virtualPaymentConfigMapper.updateById(config);
        }
        return Result.success();
    }
}
