package com.pikachu.usercenter.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.model.dto.request.admin.TeamUpdateRequestAdmin;
import com.pikachu.usercenter.model.dto.request.admin.UserUpdateRequestAdmin;
import com.pikachu.usercenter.model.entity.Team;
import com.pikachu.usercenter.model.entity.User;
import com.pikachu.usercenter.model.enums.ResponseCode;
import com.pikachu.usercenter.model.enums.TeamStatus;
import com.pikachu.usercenter.service.AdminService;
import com.pikachu.usercenter.service.TeamService;
import com.pikachu.usercenter.service.UserService;
import com.pikachu.usercenter.utils.Tools;
import jakarta.annotation.Resource;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    UserService userService;
    @Resource
    TeamService teamService;

    @Override
    public IPage<User> listUser(Long current, Long size) {
        Page<User> page = userService.page(new Page<>(current, size));
        page.setRecords(page.getRecords().stream().peek(user -> user.setPassword(null)).collect(Collectors.toList()));
        return page;
    }

    @Override
    public IPage<Team> listTeam(Long current, Long size) {
        Page<Team> page = teamService.page(new Page<>(current, size));
        page.setRecords(page.getRecords().stream().peek(team -> team.setPassword(null)).collect(Collectors.toList()));
        return page;
    }

    @Override
    public User updateUser(UserUpdateRequestAdmin params) {
        User user;
        try {
            user = UserUpdateRequestAdmin.toUser(params);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (!userService.updateById(user)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "更新用户信息失败");
        }
        User newUser = userService.getById(user.getId());
        newUser.setPassword(null);
        return newUser;
    }

    @Override
    public Team updateTeam(TeamUpdateRequestAdmin params) {
        Team team = teamService.getTeamIfExist(params.getId());

        // 队伍人数不可少于当前队伍已有人数
        if (params.getMaxNumber() != null
                && teamService.getTeamMembers(team.getId()).size() > params.getMaxNumber()) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "队伍最大人数不可少于当前队伍人数");
        }

        // 拷贝要修改的信息
        try {
            BeanUtils.copyProperties(team, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        // 是否设置为了加密队伍
        Integer status = team.getStatus();
        if (status != null) {
            TeamStatus teamStatus = TeamStatus.getEnumByValue(status);
            if (TeamStatus.SECRET.equals(teamStatus)) {
                String encryptPassword = Tools.encrypString(team.getPassword());
                team.setPassword(encryptPassword);
            }
        }

        // 更新队伍信息
        if (!teamService.updateById(team)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "修改队伍信息失败");
        }
        return teamService.getById(team.getId());
    }

}
