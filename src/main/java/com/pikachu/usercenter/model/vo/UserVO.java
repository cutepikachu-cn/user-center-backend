package com.pikachu.usercenter.model.vo;

import com.pikachu.usercenter.model.entity.User;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 用户视图对象（信息脱敏）
 */
@Data
public class UserVO implements Serializable {

    public static UserVO fromUser(User user) throws InvocationTargetException, IllegalAccessException {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userVO, user);
        return userVO;
    }

    /**
     * id
     */
    private Long id;

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
