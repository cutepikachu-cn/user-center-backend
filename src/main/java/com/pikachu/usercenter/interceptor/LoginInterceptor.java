package com.pikachu.usercenter.interceptor;

import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.model.enums.ResponseCode;
import com.pikachu.usercenter.model.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.pikachu.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LoginUserVO user = (LoginUserVO) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        return true;
    }
}
