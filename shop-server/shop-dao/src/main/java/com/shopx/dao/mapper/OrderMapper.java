package com.shopx.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.model.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT * FROM t_order WHERE order_no = #{orderNo}")
    Order selectByOrderNo(String orderNo);

    @Select("SELECT * FROM t_order WHERE order_status = 1 AND expire_time < NOW()")
    List<Order> selectExpiredAwaitingOrders();
}
