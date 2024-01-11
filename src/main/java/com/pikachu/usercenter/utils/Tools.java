package com.pikachu.usercenter.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static com.pikachu.usercenter.constant.UserConstant.SALT;

/**
 * 工具类
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
public class Tools {
    public static String encrypString(String text) {
        return DigestUtils.md5DigestAsHex((SALT + text).getBytes(StandardCharsets.UTF_8));
    }
}
