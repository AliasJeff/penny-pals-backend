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
 * 预算配置表
 * @TableName budget_config
 */
@TableName(value ="budget_config")
@Data
public class BudgetConfig implements Serializable {
    /**
     * 预算配置ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账本ID
     */
    @TableField("ledger_id")
    private Long ledgerId;

    /**
     * 用户ID（可为空，表示账本总体预算）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 预算周期
     */
    @TableField("period")
    private Object period;

    /**
     * 预算金额
     */
    @TableField("budget_amount")
    private BigDecimal budgetAmount;

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