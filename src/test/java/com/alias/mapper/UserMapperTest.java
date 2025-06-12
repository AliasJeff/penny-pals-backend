package com.alias.mapper;

import com.alias.model.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserMapperTest {

    @Resource
    private UserMapper userMapper;

    @Test
    void testGetUserById() {
        Long userId = 1L;
        User user = userMapper.getUserById(userId);
        System.out.println(user);
        Assertions.assertNotNull(user);
    }
}
