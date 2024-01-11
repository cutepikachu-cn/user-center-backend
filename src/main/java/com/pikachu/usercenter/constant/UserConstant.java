package com.pikachu.usercenter.constant;

/**
 * 常量配置
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
public interface UserConstant {
    /**
     * 用户登录状态键
     */
    String USER_LOGIN_STATE = "userLoginState";


    /**
     * 用户角色常量
     */
    int ROLE_DEFAULT = 0, ROLE_ADMIN = 1;

    /**
     * 盐值
     * 用于混淆加密密码
     */
    String SALT = "pikachu";
}
