package com.shopx.service.store;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopx.model.dto.StoreDTO;
import com.shopx.model.entity.StorePaymentQuota;
import com.shopx.model.vo.StoreVO;

import java.math.BigDecimal;

public interface StoreService {

    IPage<StoreVO> listStores(Integer status, int page, int size);

    StoreVO getStoreDetail(Long storeId);

    void createStore(StoreDTO dto);

    void updateStore(Long storeId, StoreDTO dto);

    void deleteStore(Long storeId);

    StorePaymentQuota getStoreQuota(Long storeId);

    void setStoreQuota(Long storeId, BigDecimal totalQuota);
}
