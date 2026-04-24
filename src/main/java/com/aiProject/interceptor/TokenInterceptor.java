package com.aiProject.interceptor;

import com.aiProject.common.Result;
import com.aiProject.entity.UserInfo;
import com.aiProject.util.JwtUtil;
import com.aiProject.util.UserContext;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 放行跨域预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 获取 token（支持两种格式：token / Bearer token）
        String token = request.getHeader("token");
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 3. 放行白名单
        String uri = request.getRequestURI();
        if (uri.equals("/user/login") || uri.equals("/user/captcha") || uri.equals("/user/refreshToken")) {
            return true;
        }

        // 4. 验证 token 是否有效
        if (token == null || !JwtUtil.verifyToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(JSONObject.toJSONString(Result.error("token无效或已过期，请重新登录")));
            out.flush();
            out.close();
            return false;
        }

        // 5. 解析用户 → 放入全局上下文（任意地方可拿！）
        UserInfo userInfo = JwtUtil.getUserInfoFromToken(token);
        UserContext.setCurrentUser(userInfo);

        return true;
    }

    // 请求结束 → 清除 ThreadLocal（必须）
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
