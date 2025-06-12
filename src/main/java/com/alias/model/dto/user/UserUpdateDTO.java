package com.alias.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserUpdateDTO implements Serializable {

    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 生日
     */
    private Date birthday;

    private static final long serialVersionUID = 1L;
}
