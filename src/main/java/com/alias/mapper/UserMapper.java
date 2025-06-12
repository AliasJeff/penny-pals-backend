package com.alias.mapper;

import com.alias.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author alias
 * @description 针对表【user(用户表)】的数据库操作Mapper
 * @createDate 2025-06-09 21:47:16
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);

}




