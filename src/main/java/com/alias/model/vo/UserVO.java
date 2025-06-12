package com.alias.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVO implements Serializable {

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

    /**
     * 微信开放平台ID
     */
    private String unionId;

    /**
     * 微信用户唯一标识
     */
    private String openId;

    /**
     * 用户角色
     */
    private String userRole;

    private String token;

    private static final long serialVersionUID = 1L;
}
