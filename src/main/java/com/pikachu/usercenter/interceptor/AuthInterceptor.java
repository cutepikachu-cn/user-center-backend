package com.pikachu.usercenter.interceptor;

import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.model.enums.ResponseCode;
import com.pikachu.usercenter.model.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.pikachu.usercenter.constant.UserConstant.ROLE_ADMIN;
import static com.pikachu.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 身份验证拦截器
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!isAdmin(request))
            throw new BusinessException(ResponseCode.NO_AUTH);
        return true;
    }

    /**
     * 判断当前用户是否为管理员
     *
     * @return 是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 鉴权
        LoginUserVO user = (LoginUserVO) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user != null && user.getRole() == ROLE_ADMIN;
    }
}
