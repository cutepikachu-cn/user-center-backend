package com.pikachu.usercenter.model.vo;

import com.pikachu.usercenter.model.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户登录（个人信息）视图对象
 */
@Data
public class LoginUserVO implements Serializable {

    public static LoginUserVO fromUser(User user) {
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * id
     */
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
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户角色（0：普通用户，1：管理员）
     */
    private Integer role;

    /**
     * 用户标签列表
     */
    private List<String> tags;

    @Serial
    private static final long serialVersionUID = 1L;
}
