package com.shopx.service.user;

import com.shopx.model.entity.User;

public interface UserService {

    String login(String phone, String password);

    void register(String phone, String password);

    User getUserInfo(Long userId);

    void updateUser(Long userId, String nickname, String avatar);
}
