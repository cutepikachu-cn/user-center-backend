package com.pikachu.usercenter.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签表
 *
 * @TableName tag
 */
@TableName(value = "tag")
@Data
public class Tag implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 添加该标签的用户id
     */
    private Long userId;

    /**
     * 父标签id
     */
    private Integer parentId;

    /**
     * 是否有子标签
     */
    private Boolean hasChildren;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean isDelete;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
