package com.pikachu.usercenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.model.dto.request.UserLoginRequest;
import com.pikachu.usercenter.model.dto.request.UserRegisterRequest;
import com.pikachu.usercenter.model.dto.request.UserUpdateRequest;
import com.pikachu.usercenter.model.dto.response.BaseResponse;
import com.pikachu.usercenter.model.enums.ResponseCode;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册参数对象
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        String account = userRegisterRequest.getAccount();
        String password = userRegisterRequest.getPassword();

        Long newUserId = userService.userRegister(account, password);
        return ResultUtils.success(newUserId, "注册成功");
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录参数对象
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody @Valid UserLoginRequest userLoginRequest,
                                               HttpServletRequest request) {
        String account = userLoginRequest.getAccount();
        String password = userLoginRequest.getPassword();

        LoginUserVO user = userService.userLogin(account, password, request);
        return ResultUtils.success(user, "登陆成功");
    }

    /**
     * 用户退出登录
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<?> userLogout(HttpServletRequest request) {
        userService.userLogout(request);
        return ResultUtils.success(true, "退出登录成功");
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<LoginUserVO> getCurrentUser(HttpServletRequest request) {
        LoginUserVO user = userService.getCurrentLoginUser(request);
        ;
        return ResultUtils.success(user, "当前用户");
    }

    /**
     * 搜索用户
     *
     * @param nickname 用于搜索的昵称
     * @param tags     用于搜索的标签列表
     * @param current  页数
     * @param size     每页记录数
     * @return
     */
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

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest 更新用户信息参数对象
     * @param request
     * @return
     */
    @PutMapping("/update")
    public BaseResponse<LoginUserVO> updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest,
                                                HttpServletRequest request) {

        userService.updateUser(userUpdateRequest, request);
        LoginUserVO currentUser = userService.getCurrentLoginUser(request);
        return ResultUtils.success(currentUser, "修改用户信息成功");
    }

    /**
     * 匹配用户
     *
     * @param num     匹配个数
     * @param request
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<?> matchUsers(@RequestParam(defaultValue = "5") Integer num, HttpServletRequest request) {
        if (num >= 10 || num <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "超出限制");
        }
        List<UserVO> matchedUsers = userService.matchUsers(num, request);
        return ResultUtils.success(matchedUsers);
    }
}
