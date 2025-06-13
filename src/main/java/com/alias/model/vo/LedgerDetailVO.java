package com.alias.model.vo;

import com.alias.model.entity.Entry;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class LedgerDetailVO implements Serializable {

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

     private List<LedgerUserVO> members;

     private List<Entry> entries;

     private static final long serialVersionUID = 1L;

}
