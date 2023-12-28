package com.pikachu.usercenter.model.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class UserRegisterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5349340192493919694L;
    @Pattern(regexp = "^[\\w-]{4,16}$", message = "账户必须为4~16位字母、数字、下划线或减号组合")
    private String account;
    @Pattern(regexp = "^[\\w-]{8,20}$", message = "密码必须为8~20位字母、数字、下划线或减号组合")
    private String password;
    @Pattern(regexp = "^[\\w-]{8,20}$", message = "校验密码必须为8~20位字母、数字、下划线或减号组合")
    private String checkPassword;

    @AssertTrue(message = "密码与校验密码必须相同")
    public boolean isPasswordMatch() {
        return password != null && password.equals(checkPassword);
    }
}
