package com.aiProject.service.impl;

import com.aiProject.entity.UserInfo;
import com.aiProject.mapper.UserMapper;
import com.aiProject.service.UserService;
import com.aiProject.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public String login(String username, String password, String code) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        String correctCaptcha = (String) session.getAttribute("captcha");

        if (code == null || !code.equalsIgnoreCase(correctCaptcha)) {
            throw new RuntimeException("验证码错误！");
        }

        UserInfo userInfo = userMapper.getByUsername(username);
        if (userInfo == null) {
            throw new RuntimeException("用户不存在！");
        }

        if (!userInfo.getPassword().equals(password)) {
            throw new RuntimeException("密码错误！");
        }

        if (userInfo.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用！");
        }

        session.removeAttribute("captcha");
        return JwtUtil.generateToken(userInfo);
    }
}
