package com.pikachu.usercenter.exception;

import com.pikachu.usercenter.model.enums.ResponseCode;

import java.io.Serial;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2907122813603460536L;
    private final Integer code;
    private final String description;

    public BusinessException(Integer code, String description) {
        super(description);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ResponseCode responseCode, String description) {
        super(description);
        this.code = responseCode.getCode();
        this.description = description;
    }

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getDescription());
        this.code = responseCode.getCode();
        this.description = responseCode.getDescription();
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
