package com.alias.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 账本表
 * @TableName ledger
 */
@TableName(value ="ledger")
@Data
public class Ledger implements Serializable {
    /**
     * 账本ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账本名称
     */
    @TableField("name")
    private String name;

    /**
     * 账本图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 账本描述
     */
    @TableField("description")
    private String description;

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