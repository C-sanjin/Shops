package com.shopx.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopx.common.exception.BizException;
import com.shopx.common.result.ResultCode;
import com.shopx.common.utils.JwtUtil;
import com.shopx.dao.mapper.UserMapper;
import com.shopx.model.entity.User;
import com.shopx.service.user.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String login(String phone, String password) {
        User user = userMapper.selectByPhone(phone);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BizException(ResultCode.USER_PASSWORD_ERROR);
        }
        return jwtUtil.generateToken(user.getId(), "user");
    }

    @Override
    public void register(String phone, String password) {
        User existUser = userMapper.selectByPhone(phone);
        if (existUser != null) {
            throw new BizException(ResultCode.USER_PHONE_EXISTS);
        }
        User user = new User();
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public User getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }
        String phone = user.getPhone();
        if (phone != null && phone.length() >= 7) {
            user.setPhone(phone.substring(0, 3) + "****" + phone.substring(7));
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateUser(Long userId, String nickname, String avatar) {
        User user = new User();
        user.setId(userId);
        user.setNickname(nickname);
        user.setAvatar(avatar);
        userMapper.updateById(user);
    }
}
