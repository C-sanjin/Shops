package com.shopx.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM t_user WHERE phone = #{phone}")
    User selectByPhone(String phone);
}
