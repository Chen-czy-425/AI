package com.aiProject.util;

import com.aiProject.entity.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // ====================== 核心常量 ======================
    /**
     * 密钥：自己随便写一串复杂字符串
     */
    private static final String SECRET = "mySecretKey123456789_aiProject_2025";

    /**
     * Token 过期时间 7天
     */
    public static final long EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;

    // ====================== 生成 Token（传入 UserInfo） ======================
    public static String generateToken(UserInfo userInfo) {
        return Jwts.builder()
                .setClaims(getUserClaims(userInfo)) // 把用户信息放进token
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME)) // 过期时间
                .signWith(SignatureAlgorithm.HS512, SECRET) // 加密算法
                .compact();
    }

    // ====================== 封装【全部用户信息】到 Token ======================
    private static Map<String, Object> getUserClaims(UserInfo user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("nickname", user.getNickname());
        claims.put("password", user.getPassword());
        claims.put("phone", user.getPhone());
        claims.put("email", user.getEmail());
        claims.put("avatar", user.getAvatar());
        claims.put("role", user.getRole());
        claims.put("status", user.getStatus());
        claims.put("modelLimit", user.getModelLimit());
        claims.put("dailyRequestLimit", user.getDailyRequestLimit());
        return claims;
    }

    // ====================== 解析 Token 拿到【完整 UserInfo】 ======================
    public static UserInfo getUserInfoFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();

        UserInfo user = new UserInfo();
        user.setId(Long.valueOf(claims.get("userId").toString()));
        user.setUsername(claims.get("username").toString());
        user.setNickname(claims.get("nickname") == null ? null : claims.get("nickname").toString());
        user.setPassword(claims.get("password").toString());
        user.setPhone(claims.get("phone") == null ? null : claims.get("phone").toString());
        user.setEmail(claims.get("email") == null ? null : claims.get("email").toString());
        user.setAvatar(claims.get("avatar") == null ? null : claims.get("avatar").toString());
        user.setRole((Integer) claims.get("role"));
        user.setStatus((Integer) claims.get("status"));
        user.setModelLimit((Integer) claims.get("modelLimit"));
        user.setDailyRequestLimit((Integer) claims.get("dailyRequestLimit"));


        return user;
    }

    // ====================== 验证 Token 是否有效 ======================
    public static boolean verifyToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ====================== 刷新 Token ======================
    public static String refreshToken(String oldToken) {
        if (verifyToken(oldToken)) {
            UserInfo user = getUserInfoFromToken(oldToken);
            return generateToken(user);
        }
        return null;
    }
}
