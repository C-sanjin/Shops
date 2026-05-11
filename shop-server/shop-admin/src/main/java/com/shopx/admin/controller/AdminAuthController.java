package com.shopx.admin.controller;

import com.shopx.common.result.Result;
import com.shopx.common.result.ResultCode;
import com.shopx.common.utils.JwtUtil;
import com.shopx.dao.mapper.AdminMapper;
import com.shopx.model.entity.Admin;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final AdminMapper adminMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminAuthController(AdminMapper adminMapper, JwtUtil jwtUtil) {
        this.adminMapper = adminMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        Admin admin = adminMapper.selectByUsername(username);
        if (admin == null) {
            return Result.fail(ResultCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            return Result.fail(ResultCode.USER_PASSWORD_ERROR);
        }
        String token = jwtUtil.generateToken(admin.getId(), "ADMIN");
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("adminId", admin.getId());
        data.put("username", admin.getUsername());
        data.put("realName", admin.getRealName());
        data.put("role", admin.getRole());
        return Result.success(data);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = Long.parseLong(auth.getName());
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            return Result.fail(ResultCode.USER_NOT_FOUND);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", admin.getId());
        data.put("username", admin.getUsername());
        data.put("realName", admin.getRealName());
        data.put("phone", admin.getPhone());
        data.put("role", admin.getRole());
        data.put("storeId", admin.getStoreId());
        return Result.success(data);
    }
}
