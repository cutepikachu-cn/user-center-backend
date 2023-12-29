package com.pikachu.usercenter.constant;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
public interface UserConstant {
    /**
     * 用户登录状态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    int ROLE_DEFAULT = 0, ROLE_ADMIN = 1;

    /**
     * 盐值
     * 用于混淆加密密码
     */
    String SALT = "pikachu";
}
