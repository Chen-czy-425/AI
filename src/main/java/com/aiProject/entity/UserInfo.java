package com.aiProject.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户信息实体类（原生 MyBatis 版，无 Plus）
 */
@Data
public class UserInfo {

    // 用户ID（主键，自增）
    private Long id;

    // 用户名/登录账号
    private String username;

    // 用户昵称
    private String nickname;

    // 密码（加密存储）
    private String password;

    // 手机号
    private String phone;

    // 邮箱
    private String email;

    // 头像URL
    private String avatar;

    // 角色：1普通用户 2管理员 3VIP
    private Integer role;

    // 状态：1正常 0禁用 2未激活
    private Integer status;

    // 可使用模型数量：-1=无限制
    private Integer modelLimit;

    // 每日请求次数上限
    private Integer dailyRequestLimit;

    // 创建时间
    private LocalDateTime createdAt;

    // 更新时间
    private LocalDateTime updatedAt;
}
