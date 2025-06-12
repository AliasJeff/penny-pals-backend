package com.alias.model.entity;

import com.alias.model.enums.UserRoleEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码（加密）
     */
    @TableField("password")
    private String password;

    /**
     * 用户头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 邮箱地址
     */
    @TableField("email")
    private String email;

    /**
     * 手机号
     */
    @TableField("phone_number")
    private String phoneNumber;

    /**
     * 生日
     */
    @TableField("birthday")
    private Date birthday;

    /**
     * 微信开放平台ID
     */
    @TableField("union_id")
    private String unionId;

    /**
     * 微信用户唯一标识
     */
    @TableField("open_id")
    private String openId;

    /**
     * 用户角色（user/admin/ban）
     */
    @TableField("user_role")
    private String userRole;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 删除时间
     */
    @TableField("delete_time")
    private Date deleteTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public boolean isAdmin() {
        return UserRoleEnum.ADMIN.getValue().equals(this.userRole);
    }
}