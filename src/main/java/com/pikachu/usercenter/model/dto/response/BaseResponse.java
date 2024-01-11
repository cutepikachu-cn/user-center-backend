package com.pikachu.usercenter.model.dto.response;

import com.pikachu.usercenter.model.enums.ResponseCode;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 基础响应体对象类
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 7542569996437749866L;
    private Integer code;
    private String message;
    private T data;

    public BaseResponse(ResponseCode code, String message, T data) {
        this.code = code.getCode();
        this.message = message;
        this.data = data;
    }

    public BaseResponse(ResponseCode code, String message) {
        this.code = code.getCode();
        this.message = message;
    }

    public BaseResponse(ResponseCode code, T data) {
        this.code = code.getCode();
        this.data = data;
    }

    public BaseResponse(ResponseCode code) {
        this.code = code.getCode();
        this.message = code.getDescription();
    }

    public BaseResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(Integer code, T data) {
        this.code = code;
        this.data = data;
    }
}
