package com.aiProject.service;

import com.aiProject.entity.UserInfo;

public interface UserService {
    String login(String username, String password, String code);
}
