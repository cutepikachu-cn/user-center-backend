package com.pikachu.usercenter.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.pikachu.usercenter.typehandler.StringListTypeHandler;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍表
 *
 * @TableName team
 */
@TableName(value = "team")
@Data
public class Team implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 队伍最大人数
     */
    private Integer maxNumber;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 队长id
     */
    private Long userId;

    /**
     * 队伍创建用户id
     */
    private Long createUserId;

    /**
     * 队伍状态（默认0 ~ 公开；1 ~ 私有；2 ~ 加密）
     */
    private Integer status;

    /**
     * 队伍密码（加密队伍需密码加入）
     */
    private String password;

    /**
     * 用户标签列表，以`,`分割
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> tags;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean isDelete;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
