package com.aiProject.util;

import com.aiProject.entity.UserInfo;

public class UserContext {

    // 线程隔离：每个请求独立，绝对不会串用户
    private static final ThreadLocal<UserInfo> USER_THREAD_LOCAL = new ThreadLocal<>();

    // 放入当前用户
    public static void setCurrentUser(UserInfo user) {
        USER_THREAD_LOCAL.set(user);
    }

    // 全局任意地方获取当前登录用户
    public static UserInfo getCurrentUser() {
        return USER_THREAD_LOCAL.get();
    }

    // 清除（必须，防止内存泄漏）
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}
