package com.aiProject.controller;

import com.aiProject.common.Result;
import com.aiProject.service.UserService;
import com.aiProject.util.JwtUtil;
import com.aiProject.dto.LoginDTO;
import com.aiProject.util.CaptchaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
public class UserController {

    @Autowired
    private UserService userService;

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

        log.info("生成验证码 - 验证码: {}, SessionID: {}", verifyCode, session.getId());
        // 4. 存入 session
        session.setAttribute("captcha", verifyCode);
    }

    /**
     * 2. 登录接口（带验证码校验）
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            String token = userService.login(loginDTO,request);
            return Result.success(token);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }

    }

    @GetMapping("/refreshToken")
    public Result<String> refreshToken(
            @RequestHeader("Authorization") String authHeader
    ) {

        String oldToken = authHeader.replace("Bearer ", "");

        String newToken = JwtUtil.refreshToken(oldToken);

        if (newToken != null) {
            return Result.success(newToken);
        }

        return Result.error(401, "旧token无效，无法刷新，请重新登录！", null);
    }
}
