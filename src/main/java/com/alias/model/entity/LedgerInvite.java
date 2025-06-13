package com.alias.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 账本邀请表
 * @TableName ledger_invite
 */
@TableName(value ="ledger_invite")
@Data
public class LedgerInvite implements Serializable {
    /**
     * 账本邀请ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账本ID
     */
    @TableField(value = "ledger_id")
    private Long ledgerId;

    /**
     * 邀请者ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 邀请码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 过期时间
     */
    @TableField(value = "expire_time")
    private Date expireTime;

    /**
     * 被邀请人ID
     */
    @TableField(value = "invited_user_id")
    private Long invitedUserId;

    /**
     * 删除时间
     */
    @TableField(value = "delete_time")
    private Date deleteTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}