package com.shopx.service.store.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopx.common.exception.BizException;
import com.shopx.common.result.ResultCode;
import com.shopx.dao.mapper.StoreMapper;
import com.shopx.dao.mapper.StorePaymentQuotaMapper;
import com.shopx.model.dto.StoreDTO;
import com.shopx.model.entity.Store;
import com.shopx.model.entity.StorePaymentQuota;
import com.shopx.model.vo.StoreVO;
import com.shopx.service.store.StoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreMapper storeMapper;
    private final StorePaymentQuotaMapper storePaymentQuotaMapper;

    public StoreServiceImpl(StoreMapper storeMapper,
                            StorePaymentQuotaMapper storePaymentQuotaMapper) {
        this.storeMapper = storeMapper;
        this.storePaymentQuotaMapper = storePaymentQuotaMapper;
    }

    @Override
    public IPage<StoreVO> listStores(Integer status, int page, int size) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Store::getStatus, status);
        }
        wrapper.eq(Store::getDeleted, 0);
        wrapper.orderByDesc(Store::getCreatedAt);

        Page<Store> storePage = new Page<>(page, size);
        IPage<Store> result = storeMapper.selectPage(storePage, wrapper);

        return result.convert(store -> {
            StoreVO vo = new StoreVO();
            BeanUtils.copyProperties(store, vo);
            return vo;
        });
    }

    @Override
    public StoreVO getStoreDetail(Long storeId) {
        Store store = storeMapper.selectById(storeId);
        if (store == null || store.getDeleted() == 1) {
            throw new BizException(ResultCode.STORE_NOT_FOUND);
        }
        StoreVO vo = new StoreVO();
        BeanUtils.copyProperties(store, vo);
        return vo;
    }

    @Override
    public void createStore(StoreDTO dto) {
        Store store = new Store();
        BeanUtils.copyProperties(dto, store);
        store.setStoreCode("STORE" + String.format("%03d", new Random().nextInt(1000)));
        store.setStatus(1);
        store.setDeleted(0);
        storeMapper.insert(store);

        StorePaymentQuota quota = new StorePaymentQuota();
        quota.setStoreId(store.getId());
        quota.setTotalQuota(BigDecimal.ZERO);
        quota.setUsedQuota(BigDecimal.ZERO);
        quota.setStatus(1);
        quota.setVersion(0);
        storePaymentQuotaMapper.insert(quota);
    }

    @Override
    public void updateStore(Long storeId, StoreDTO dto) {
        Store store = storeMapper.selectById(storeId);
        if (store == null || store.getDeleted() == 1) {
            throw new BizException(ResultCode.STORE_NOT_FOUND);
        }
        store.setStoreName(dto.getStoreName());
        store.setAddress(dto.getAddress());
        store.setContactPhone(dto.getContactPhone());
        store.setBusinessHours(dto.getBusinessHours());
        store.setLongitude(dto.getLongitude());
        store.setLatitude(dto.getLatitude());
        storeMapper.updateById(store);
    }

    @Override
    public void deleteStore(Long storeId) {
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            throw new BizException(ResultCode.STORE_NOT_FOUND);
        }
        store.setDeleted(1);
        storeMapper.updateById(store);
    }

    @Override
    public StorePaymentQuota getStoreQuota(Long storeId) {
        LambdaQueryWrapper<StorePaymentQuota> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StorePaymentQuota::getStoreId, storeId);
        return storePaymentQuotaMapper.selectOne(wrapper);
    }

    @Override
    public void setStoreQuota(Long storeId, BigDecimal totalQuota) {
        LambdaQueryWrapper<StorePaymentQuota> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StorePaymentQuota::getStoreId, storeId);
        StorePaymentQuota quota = storePaymentQuotaMapper.selectOne(wrapper);

        if (quota == null) {
            quota = new StorePaymentQuota();
            quota.setStoreId(storeId);
            quota.setTotalQuota(totalQuota);
            quota.setUsedQuota(BigDecimal.ZERO);
            quota.setStatus(1);
            quota.setVersion(0);
            storePaymentQuotaMapper.insert(quota);
        } else {
            quota.setTotalQuota(totalQuota);
            storePaymentQuotaMapper.updateById(quota);
        }
    }
}
