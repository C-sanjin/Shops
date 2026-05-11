package com.shopx.api.controller;

import com.shopx.common.result.Result;
import com.shopx.model.entity.User;
import com.shopx.service.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserAuthController {

    private final UserService userService;

    public UserAuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");
        userService.register(phone, password);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");
        String token = userService.login(phone, password);
        return Result.success(token);
    }

    @GetMapping("/info")
    public Result<User> info() {
        Long userId = getCurrentUserId();
        User userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Map<String, String> params) {
        Long userId = getCurrentUserId();
        String nickname = params.get("nickname");
        String avatar = params.get("avatar");
        userService.updateUser(userId, nickname, avatar);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
