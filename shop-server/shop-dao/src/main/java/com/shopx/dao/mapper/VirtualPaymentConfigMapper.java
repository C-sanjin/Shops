package com.shopx.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.model.entity.VirtualPaymentConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VirtualPaymentConfigMapper extends BaseMapper<VirtualPaymentConfig> {

    @Select("SELECT * FROM t_virtual_payment_config WHERE status = 1 AND (store_id = 0 OR store_id = #{storeId}) AND (expire_time IS NULL OR expire_time > NOW())")
    List<VirtualPaymentConfig> selectActiveByStoreId(Long storeId);
}
