package com.pikachu.usercenter.utils;

import com.pikachu.usercenter.model.dto.response.BaseResponse;
import com.pikachu.usercenter.model.enums.ResponseCode;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
public class ResultUtils {
    private ResultUtils() {
    }

    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(ResponseCode.SUCCESS, message, data);
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(ResponseCode.SUCCESS, ResponseCode.SUCCESS.getDescription(), data);
    }

    public static <T> BaseResponse<T> success(String message) {
        return new BaseResponse<>(ResponseCode.SUCCESS, message, null);
    }

    public static <T> BaseResponse<T> error(Integer errorCode, String message) {
        return new BaseResponse<>(errorCode, message);
    }

    public static <T> BaseResponse<T> error(ResponseCode errorCode, String message) {
        return new BaseResponse<>(errorCode, message);
    }

    public static <T> BaseResponse<T> error(ResponseCode errorCode) {
        return new BaseResponse<>(errorCode, errorCode.getDescription());
    }
}
