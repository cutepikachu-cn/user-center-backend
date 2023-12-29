package com.pikachu.usercenter.interceptor;

import com.pikachu.usercenter.common.ResponseCode;
import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.model.vo.LoginUserVO;
import com.pikachu.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.pikachu.usercenter.constant.UserConstant.ROLE_ADMIN;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
public class AuthInterceptor implements HandlerInterceptor {
    @Resource
    private UserService userService;

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
        LoginUserVO user = userService.getCurrentLoginUser(request);
        ;
        return user != null && user.getRole() == ROLE_ADMIN;
    }
}
