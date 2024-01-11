package com.pikachu.usercenter.model.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求参数
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class UserLoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 2998317931158415205L;
    @Pattern(regexp = "^[\\w-]+$", message = "账户必须为4~16位字母、数字、下划线或减号组合")
    private String account;
    @Pattern(regexp = "^[\\w-]+$", message = "密码必须为8~20位字母、数字、下划线或减号组合")
    private String password;
}
