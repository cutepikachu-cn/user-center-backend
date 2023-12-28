package com.pikachu.usercenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pikachu.usercenter.model.dto.request.UserLoginRequest;
import com.pikachu.usercenter.model.dto.request.UserRegisterRequest;
import com.pikachu.usercenter.model.dto.request.UserUpdateRequest;
import com.pikachu.usercenter.model.dto.response.BaseResponse;
import com.pikachu.usercenter.model.vo.LoginUserVO;
import com.pikachu.usercenter.model.vo.UserVO;
import com.pikachu.usercenter.service.UserService;
import com.pikachu.usercenter.utils.ResultUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.pikachu.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        String account = userRegisterRequest.getAccount();
        String password = userRegisterRequest.getPassword();

        Long newUserId = userService.userRegister(account, password);
        return ResultUtils.success(newUserId, "注册成功");
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody @Valid UserLoginRequest userLoginRequest,
                                               HttpServletRequest request) {
        String account = userLoginRequest.getAccount();
        String password = userLoginRequest.getPassword();

        LoginUserVO user = userService.userLogin(account, password, request);
        return ResultUtils.success(user, "登陆成功");
    }

    @PostMapping("/logout")
    public BaseResponse<?> userLogout(HttpServletRequest request) {
        userService.userLogout(request);
        return ResultUtils.success("退出登录成功");
    }

    @GetMapping("/current")
    public BaseResponse<LoginUserVO> getCurrentUser(HttpServletRequest request) {
        LoginUserVO user = ((LoginUserVO) request.getSession().getAttribute(USER_LOGIN_STATE));
        return ResultUtils.success(user, "当前用户");
    }

    @GetMapping("/search")
    public BaseResponse<IPage<UserVO>> searchUsers(String nickname,
                                                   String[] tags,
                                                   @RequestParam(defaultValue = "1") Long current,
                                                   @RequestParam(defaultValue = "5") Long size) {
        Map<String, Object> conditions = new HashMap<>();

        if (!StringUtils.isBlank(nickname)) {
            conditions.put("nickname", nickname);
        } else if (tags != null && tags.length != 0) {
            conditions.put("tags", Arrays.stream(tags).map(String::toLowerCase).collect(Collectors.toList()));
        }

        IPage<UserVO> userPage = userService.searchUsers(conditions, current, size);
        return ResultUtils.success(userPage);
    }

    @PutMapping("/update")
    public BaseResponse<LoginUserVO> updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest,
                                                HttpServletRequest request) {

        userService.updateUser(userUpdateRequest, request);
        LoginUserVO currentUser = (LoginUserVO) request.getSession().getAttribute(USER_LOGIN_STATE);
        return ResultUtils.success(currentUser, "修改用户信息成功");
    }
}
