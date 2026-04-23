package com.aiProject.controller;

import com.aiProject.common.Result;
import com.aiProject.dto.LoginDTO;
import com.aiProject.util.CaptchaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin
public class UserController {

    /**
     * 1. 生成验证码图片
     * 前端直接访问这个接口就能显示图片
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 先获取 session（必须在输出图片之前！！）
        HttpSession session = request.getSession();

        // 2. 设置响应头
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        // 3. 生成验证码
        String verifyCode = CaptchaUtil.generateCaptcha(response.getOutputStream());

        // 4. 存入 session
        session.setAttribute("captcha", verifyCode);
    }

    /**
     * 2. 登录接口（带验证码校验）
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        String code = loginDTO.getCode();

        HttpSession session = request.getSession();
        String correctCaptcha = (String) session.getAttribute("captcha");

        // 1. 校验验证码
        if (code == null || !code.equalsIgnoreCase(correctCaptcha)) {
            return Result.error("验证码错误！");
        }

        // 2. 校验用户名密码
        if ("admin".equals(username) && "123456".equals(password)) {
            session.removeAttribute("captcha");
            return Result.success("登录成功！");
        }

        return Result.error("用户名或密码错误！");
    }
}
