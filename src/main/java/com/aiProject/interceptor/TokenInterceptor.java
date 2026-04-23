package com.aiProject.interceptor;

import com.aiProject.common.Result;
import com.aiProject.entity.UserInfo;
import com.aiProject.util.JwtUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * JWT Token拦截器：验证需要权限的接口
 */
public class TokenInterceptor implements HandlerInterceptor {

    /**
     * 接口请求前拦截（验证token）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 放行 OPTIONS 预检请求（解决跨域报错）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 放行登录、验证码接口（精确匹配，更安全）
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/user/login") || requestURI.equals("/user/captcha") || requestURI.equals("/user/refreshToken")) {
            return true;
        }

        // 3. 从请求头获取 token
        String token = request.getHeader("Authorization");

        // 4. 处理前端传的 Bearer token 格式（最关键！）
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer "
        }

        // 5. 校验 token 是否存在 + 是否合法
        if (token == null || !JwtUtil.verifyToken(token)) {
            // 设置 401 未授权状态码
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            // 用 fastjson 优雅返回，避免手写字符串出错
            Result<Object> result = Result.error("token无效或已过期，请重新登录！");
            out.write(JSONObject.toJSONString(result));
            out.flush();
            out.close();
            return false;
        }

        UserInfo loginUser = JwtUtil.getUserInfoFromToken(token);
        request.setAttribute("loginUser", loginUser);
        return true;
    }
}
