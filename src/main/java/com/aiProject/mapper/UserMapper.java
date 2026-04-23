package com.aiProject.mapper;

import com.aiProject.entity.UserInfo;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user_info WHERE username = #{username}")
    UserInfo getByUsername(String username);
}
