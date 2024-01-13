package com.pikachu.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.model.dto.request.admin.TeamUpdateRequestAdmin;
import com.pikachu.usercenter.model.dto.request.admin.UserUpdateRequestAdmin;
import com.pikachu.usercenter.model.entity.Team;
import com.pikachu.usercenter.model.entity.TeamUser;
import com.pikachu.usercenter.model.entity.User;
import com.pikachu.usercenter.model.enums.ResponseCode;
import com.pikachu.usercenter.model.enums.TeamStatus;
import com.pikachu.usercenter.service.AdminService;
import com.pikachu.usercenter.service.TeamService;
import com.pikachu.usercenter.service.TeamUserService;
import com.pikachu.usercenter.service.UserService;
import com.pikachu.usercenter.utils.Tools;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Resource
    UserService userService;
    @Resource
    TeamService teamService;
    @Resource
    TeamUserService teamUserService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public IPage<User> listUser(Long current, Long size) {
        String redisKey = String.format("user-center:admin:list-user-%d-%d", current, size);
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        IPage<User> userIPage = (IPage<User>) ops.get(redisKey);
        if (userIPage == null) {
            userIPage = userService.page(new Page<>(current, size));
            try {
                ops.set(redisKey, userIPage, 1, TimeUnit.DAYS);
            } catch (Exception e) {
                log.error("redis set key error", e);
            }
        }
        userIPage.setRecords(userIPage.getRecords().stream().peek(user -> user.setPassword(null)).collect(Collectors.toList()));
        return userIPage;
    }

    @Override
    public IPage<Team> listTeam(Long current, Long size) {
        String redisKey = String.format("user-center:admin:list-team-%d-%d", current, size);
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        IPage<Team> teamIPage = (IPage<Team>) ops.get(redisKey);
        if (teamIPage == null) {
            teamIPage = teamService.page(new Page<>(current, size));
            try {
                ops.set(redisKey, teamIPage, 1, TimeUnit.DAYS);
            } catch (Exception e) {
                log.error("redis set key error", e);
            }
        }
        teamIPage.setRecords(teamIPage.getRecords().stream().peek(team -> team.setPassword(null)).collect(Collectors.toList()));
        return teamIPage;
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
        removeListUserCache();
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

        removeListUserCache();
        return teamService.getById(team.getId());
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }

        // 删除用户
        if (!userService.removeById(userId)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        }

        // 删除用户相关队伍
        LambdaQueryWrapper<Team> teamLQW = new LambdaQueryWrapper<>();
        teamLQW.eq(Team::getUserId, userId);
        teamService.remove(teamLQW);
        LambdaQueryWrapper<TeamUser> teamUserLQW = new LambdaQueryWrapper<>();
        teamUserLQW.eq(TeamUser::getUserId, userId);
        teamUserService.remove(teamUserLQW);

        removeListUserCache();

    }

    @Override
    public void deleteTeam(Long teamId) {
        Team team = teamService.getById(teamId);
        if (team == null) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        }

        // 删除队伍
        LambdaQueryWrapper<Team> teamLQW = new LambdaQueryWrapper<>();
        teamLQW.eq(Team::getId, teamId);
        if (!teamService.remove(teamLQW)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        }

        // 删除队伍用户关系
        LambdaQueryWrapper<TeamUser> teamUserLQW = new LambdaQueryWrapper<>();
        teamUserLQW.eq(TeamUser::getTeamId, teamId);
        if (!teamUserService.remove(teamUserLQW)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        }

        removeListTeamCache();

    }

    private void removeListUserCache() {
        BoundValueOperations<String, Object> boundOps = redisTemplate.boundValueOps("");
        ScanOptions scanOptions = ScanOptions.scanOptions().match("user-center:admin:list-user-*").build();

        Set<String> keys = new HashSet<>();
        Cursor<String> cursor = boundOps.getOperations().scan(scanOptions);
        while (cursor.hasNext()) {
            String key = cursor.next();
            keys.add(key);
        }
        redisTemplate.delete(keys);
    }

    private void removeListTeamCache() {
        BoundValueOperations<String, Object> boundOps = redisTemplate.boundValueOps("");
        ScanOptions scanOptions = ScanOptions.scanOptions().match("user-center:admin:list-team-*").build();

        Set<String> keys = new HashSet<>();
        Cursor<String> cursor = boundOps.getOperations().scan(scanOptions);
        while (cursor.hasNext()) {
            String key = cursor.next();
            keys.add(key);
        }
        redisTemplate.delete(keys);
    }

}
