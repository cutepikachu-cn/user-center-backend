package com.pikachu.usercenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.model.dto.response.BaseResponse;
import com.pikachu.usercenter.model.entity.User;
import com.pikachu.usercenter.model.enums.ResponseCode;
import com.pikachu.usercenter.service.UserService;
import com.pikachu.usercenter.utils.ResultUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public BaseResponse<IPage<User>> listUsers(@RequestParam(defaultValue = "1") Long current,
                                               @RequestParam(defaultValue = "5") Long pageSize) {
        IPage<User> userPage = userService.pageUsers(current, pageSize);
        return ResultUtils.success(userPage);
    }

    @PutMapping("/update")
    public BaseResponse<?> updateUser(@RequestBody(required = false) User user) {
        if (user == null || user.getId() == null || user.getId() <= 0)
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        return userService.updateById(user) ? ResultUtils.success("修改用户信息成功")
                : ResultUtils.error(ResponseCode.PARAMS_ERROR, "修改用户信息失败");
    }

    @DeleteMapping("/delete")
    public BaseResponse<?> deleteUser(Long id) {
        if (id == null || id <= 0 || !userService.removeById(id))
            return ResultUtils.error(ResponseCode.PARAMS_ERROR, "删除失败");

        return ResultUtils.success(id, "删除成功");
    }
}
