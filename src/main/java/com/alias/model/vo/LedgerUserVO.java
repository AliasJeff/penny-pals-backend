package com.alias.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LedgerUserVO implements Serializable {

    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账本ID
     */
    @TableField("ledger_id")
    private Long ledgerId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 权限角色
     */
    @TableField("role")
    private String role;

    /**
     * 删除时间
     */
    @TableField("delete_time")
    private Date deleteTime;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
