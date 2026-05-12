package com.aiProject.service;

import com.aiProject.dto.LoginDTO;
import com.aiProject.entity.UserInfo;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    String login(LoginDTO loginDTO, HttpServletRequest request);
}
