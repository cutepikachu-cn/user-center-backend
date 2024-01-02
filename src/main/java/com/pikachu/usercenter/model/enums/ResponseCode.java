package com.pikachu.usercenter.model.enums;

import lombok.Getter;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Getter
public enum ResponseCode {
    SUCCESS(0, "成功"),
    PARAMS_ERROR(40000, "参数错误"),
    NOT_LOGIN(40100, "未登录"),
    NO_AUTH(40101, "无权限"),
    SYSTEM_ERROR(50001, "系统内部错误");

    private final Integer code;
    private final String description;

    ResponseCode(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
