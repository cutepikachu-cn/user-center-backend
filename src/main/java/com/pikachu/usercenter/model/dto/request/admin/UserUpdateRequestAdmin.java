package com.pikachu.usercenter.model.dto.request.admin;

import com.pikachu.usercenter.model.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class UserUpdateRequestAdmin implements Serializable {

    public static User toUser(UserUpdateRequestAdmin userUpdateRequestAdmin) throws InvocationTargetException, IllegalAccessException {
        User user = new User();
        BeanUtils.copyProperties(user, userUpdateRequestAdmin);

        return user;
    }

    @Serial
    private static final long serialVersionUID = 6238696263458397470L;

    @NotNull(message = "用户id不能为空")
    private Long id;
    @Length(max = 30, message = "昵称长度必须在0~30")
    private String nickname;
    @Length(max = 1024, message = "头像URL长度必须在0~1024")
    private String avatarUrl;
    @Length(max = 512, message = "个人介绍长度必须在0~512")
    private String profile;
    private Boolean gender;
    @Range(min = 1, max = 200, message = "年龄必须在1~200")
    private Integer age;
    @Length(min = 5, max = 20, message = "手机号长度必须在5~20之间")
    private String phone;
    @Length(min = 5, max = 64, message = "邮箱长度必须在5~64之间")
    private String email;
    @Range(min = 0, max = 0, message = "用户状态（0：正常）")
    private Integer status;
    @Range(min = 0, max = 1, message = "用户角色（0：普通用户，1：管理员）")
    private Integer role;
    private List<String> tags;
}
