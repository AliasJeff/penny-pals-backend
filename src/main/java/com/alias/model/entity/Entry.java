package com.alias.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 账目记录表
 * @TableName entry
 */
@TableName(value ="entry")
@Data
public class Entry implements Serializable {
    /**
     * 账目ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账本ID
     */
    @TableField("ledger_id")
    private Long ledgerId;

    /**
     * 记录人ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 账目类型
     */
    @TableField("type")
    private String type;

    /**
     * 分类（如餐饮、交通）
     */
    @TableField("category")
    private String category;

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 记账日期
     */
    @TableField("date")
    private Date date;

    /**
     * 备注
     */
    @TableField("note")
    private String note;

    /**
     * 账目图标
     */
    @TableField("icon")
    private String icon;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}