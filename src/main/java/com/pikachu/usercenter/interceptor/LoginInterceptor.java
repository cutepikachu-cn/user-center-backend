package com.pikachu.usercenter.interceptor;

import com.pikachu.usercenter.common.ResponseCode;
import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.model.vo.LoginUserVO;
import com.pikachu.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LoginUserVO user = userService.getCurrentLoginUser(request);
        if (user == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        return true;
    }
}
