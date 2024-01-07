package com.pikachu.usercenter.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.pikachu.usercenter.typehandler.StringListTypeHandler;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户表
 *
 * @TableName user
 */
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账户
     */
    private String account;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像url
     */
    private String avatarUrl;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 性别
     */
    private Boolean gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否有效（0：正常）
     */
    private Integer status;

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

    /**
     * 用户角色（0：普通用户，1：管理员）
     */
    private Integer role;

    /**
     * 用户标签列表
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> tags;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
