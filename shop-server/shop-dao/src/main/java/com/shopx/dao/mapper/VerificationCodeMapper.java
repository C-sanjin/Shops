package com.shopx.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopx.model.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {

    @Select("SELECT * FROM t_verification_code WHERE code = #{code}")
    VerificationCode selectByCode(String code);

    @Select("SELECT * FROM t_verification_code WHERE batch_no = #{batchNo}")
    List<VerificationCode> selectByBatchNo(String batchNo);
}
