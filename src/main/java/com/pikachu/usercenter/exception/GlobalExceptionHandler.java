package com.pikachu.usercenter.exception;

import com.pikachu.usercenter.model.dto.response.BaseResponse;
import com.pikachu.usercenter.model.enums.ResponseCode;
import com.pikachu.usercenter.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@RestControllerAdvice("com.pikachu.usercenter")
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class
    })
    public BaseResponse<?> httpMessageNotReadableException(Exception e) {
        return ResultUtils.error(ResponseCode.PARAMS_ERROR);
    }

    @ExceptionHandler({BusinessException.class})
    public BaseResponse<?> businessException(BusinessException e) {
        return ResultUtils.error(e.getCode(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeException(RuntimeException e) {
        return ResultUtils.error(ResponseCode.SYSTEM_ERROR);
    }
}
