package com.pikachu.usercenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pikachu.usercenter.model.dto.request.admin.TeamUpdateRequestAdmin;
import com.pikachu.usercenter.model.dto.request.admin.UserUpdateRequestAdmin;
import com.pikachu.usercenter.model.dto.response.BaseResponse;
import com.pikachu.usercenter.model.entity.Team;
import com.pikachu.usercenter.model.entity.User;
import com.pikachu.usercenter.service.AdminService;
import com.pikachu.usercenter.utils.ResultUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    @GetMapping("/user/list")
    public BaseResponse<IPage<User>> listUser(@RequestParam(defaultValue = "1") Long current,
                                              @RequestParam(defaultValue = "5") Long size) {
        IPage<User> userIPage = adminService.listUser(current, size);
        return ResultUtils.success(userIPage);
    }

    @GetMapping("/team/list")
    public BaseResponse<IPage<Team>> listTeam(@RequestParam(defaultValue = "1") Long current,
                                              @RequestParam(defaultValue = "5") Long size) {
        IPage<Team> teamIPage = adminService.listTeam(current, size);
        return ResultUtils.success(teamIPage);
    }

    @PutMapping("/user/update")
    public BaseResponse<User> updateUser(@RequestBody @Valid UserUpdateRequestAdmin userUpdateRequestAdmin) {
        User newUser = adminService.updateUser(userUpdateRequestAdmin);
        return ResultUtils.success(newUser);
    }

    @PutMapping("/team/update")
    public BaseResponse<Team> updateTeam(@RequestBody @Valid TeamUpdateRequestAdmin teamUpdateRequestAdmin) {
        Team newTeam = adminService.updateTeam(teamUpdateRequestAdmin);
        return ResultUtils.success(newTeam);
    }

}
