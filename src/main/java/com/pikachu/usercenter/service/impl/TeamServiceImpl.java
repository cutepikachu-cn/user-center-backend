package com.pikachu.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.mapper.TeamMapper;
import com.pikachu.usercenter.model.dto.request.TeamCreateRequest;
import com.pikachu.usercenter.model.dto.request.TeamUpdateRequest;
import com.pikachu.usercenter.model.entity.Team;
import com.pikachu.usercenter.model.entity.TeamUser;
import com.pikachu.usercenter.model.entity.User;
import com.pikachu.usercenter.model.enums.ResponseCode;
import com.pikachu.usercenter.model.enums.TeamStatus;
import com.pikachu.usercenter.model.vo.LoginUserVO;
import com.pikachu.usercenter.model.vo.TeamUserVO;
import com.pikachu.usercenter.model.vo.UserVO;
import com.pikachu.usercenter.service.TeamService;
import com.pikachu.usercenter.service.TeamUserService;
import com.pikachu.usercenter.service.UserService;
import com.pikachu.usercenter.utils.Tools;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 28944
 * @description 针对表【team(队伍表)】的数据库操作Service实现
 * @createDate 2023-12-27 20:04:43
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    TeamUserService teamUserService;

    @Resource
    UserService userService;


    @Override
    public TeamUserVO createTeam(TeamCreateRequest teamCreateRequest, HttpServletRequest request) {
        // 创建队伍对象
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, teamCreateRequest);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        Long currentUserId = userService.getCurrentLoginUser(request).getId();
        // 队伍为当前登录的用户创建的
        team.setUserId(currentUserId);

        // 是否为私密队伍
        TeamStatus teamStatus = TeamStatus.getEnumByValue(team.getStatus());
        if (TeamStatus.SECRET.equals(teamStatus)) {
            String encryptPassword = Tools.encrypString(team.getPassword());
            team.setPassword(encryptPassword);
        }

        // 存储队伍信息
        if (!save(team)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "创建队伍失败");
        }

        // 添加队伍~队员关系
        TeamUser teamUser = new TeamUser();
        teamUser.setTeamId(team.getId());
        teamUser.setUserId(currentUserId);
        if (!teamUserService.save(teamUser)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "创建队伍失败");
        }

        return getTeamUserVOById(team.getId());
    }

    @Override
    public void dismissTeam(Long teamId, HttpServletRequest request) {
        // 获取要解散的队伍
        Team team = getTeamIfExist(teamId);

        // 是否有权解散
        // 只能解散自己的队伍
        Long currentUserId = userService.getCurrentLoginUser(request).getId();
        if (!isCaptain(team, currentUserId)) {
            throw new BusinessException(ResponseCode.NO_AUTH, "仅队长可以解散队伍");
        }

        // 解散队伍
        if (!removeById(teamId)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "解散队伍失败");
        }

        // 删除用户~队伍关系表中的关系数据
        QueryWrapper<TeamUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id", teamId);
        if (!teamUserService.remove(queryWrapper)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "解散队伍失败");
        }

    }

    @Override
    public TeamUserVO updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        // 要修改信息的队伍是否存在
        Team team = getTeamIfExist(teamUpdateRequest.getId());

        // 检查是否为自己的队伍
        Long currentUserId = userService.getCurrentLoginUser(request).getId();
        if (!isCaptain(team, currentUserId)) {
            throw new BusinessException(ResponseCode.NO_AUTH, "仅队长可以更新队伍信息");
        }

        // 拷贝要修改的信息
        try {
            BeanUtils.copyProperties(team, teamUpdateRequest);
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

        // 跟新队伍信息
        if (!updateById(team)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "修改队伍信息失败");
        }

        // 返回修改后的队伍信息
        return getTeamUserVOById(team.getId());
    }

    @Override
    public TeamUserVO getTeamUserVOById(Long teamId) {
        if (teamId <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        Team team = getTeamIfExist(teamId);

        List<UserVO> teamMembers = getTeamMembers(teamId);
        TeamUserVO teamUserVO = null;
        try {
            teamUserVO = TeamUserVO.combine(team, teamMembers);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return teamUserVO;
    }

    @Override
    public IPage<TeamUserVO> searchTeams(Long current, Long size, String keyword) {
        LambdaQueryWrapper<Team> teamLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 筛除私密 / 加密队伍
        teamLambdaQueryWrapper.notIn(Team::getStatus, 1, 2);
        // 筛除已过期队伍
        teamLambdaQueryWrapper.gt(Team::getExpireTime, new Date());

        // 如果有搜索关键字，通过关键字模糊匹配
        if (!StringUtils.isBlank(keyword)) {
            teamLambdaQueryWrapper.and(qr -> qr.like(Team::getName, keyword)
                    .or().like(Team::getDescription, keyword)
                    .or().like(Team::getTags, keyword));
        }

        Page<Team> teamPage = page(new Page<>(current, size), teamLambdaQueryWrapper);
        IPage<TeamUserVO> teamUserVOIPage = teamPage.convert(team -> getTeamUserVOById(team.getId()));
        return teamUserVOIPage;
    }

    @Override
    public void joinTeam(Long teamId, String password, HttpServletRequest request) {

        // 查询队伍是否存在
        Team team = getTeamIfExist(teamId);

        // 查询是否已加入
        LoginUserVO currentUser = userService.getCurrentLoginUser(request);
        List<TeamUser> members =
                teamUserService.list(new QueryWrapper<TeamUser>().eq("team_id", teamId));
        for (TeamUser member : members) {
            if (member.getUserId().equals(currentUser.getId())) {
                throw new BusinessException(ResponseCode.PARAMS_ERROR, "已在队伍中");
            }
        }

        // 队伍是否再有效期内
        if (team.getExpireTime().before(new Date())) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "组队有效期已过");
        }

        // 队伍是否满员
        if (isTeamFull(team, members)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "队伍已满员");
        }

        // 是否为加密队伍
        TeamStatus teamStatus = TeamStatus.getEnumByValue(team.getStatus());
        if (TeamStatus.SECRET.equals(teamStatus)) {
            if (StringUtils.isBlank(password)) {
                throw new BusinessException(ResponseCode.PARAMS_ERROR, "密码错误");
            }
            String encryptPassword = Tools.encrypString(password);
            if (!encryptPassword.equals(team.getPassword())) {
                throw new BusinessException(ResponseCode.PARAMS_ERROR, "密码错误");
            }
        } else if (TeamStatus.PRIVATE.equals(teamStatus)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "无法加入私密队伍");
        }

        // 在队伍~用户关系表添加字段
        TeamUser teamUser = new TeamUser();
        teamUser.setTeamId(teamId);
        teamUser.setUserId(currentUser.getId());
        if (!teamUserService.save(teamUser)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "加入队伍失败");
        }

    }

    @Override
    public void exitTeam(Long teamId, HttpServletRequest request) {
        // 判断队伍是否存在
        Team team = getTeamIfExist(teamId);

        // 判断是否为队长
        Long currentUserId = userService.getCurrentLoginUser(request).getId();
        if (Objects.equals(team.getUserId(), currentUserId)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "队长不可退出队伍");
        }

        LambdaQueryWrapper<TeamUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamUser::getUserId, currentUserId);
        queryWrapper.eq(TeamUser::getTeamId, teamId);

        if (!teamUserService.remove(queryWrapper)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "退出队伍失败");
        }

    }

    /**
     * @param team    队伍对象
     * @param members 队伍~成员关系对象列表
     */
    private boolean isTeamFull(Team team, List<TeamUser> members) {
        return team.getMaxNumber() == members.size();
    }

    /**
     * 根据队伍 id 查询队伍是否存在
     *
     * @param teamId 队伍 id
     * @return
     */
    private Team getTeamIfExist(Long teamId) {
        Team team = getById(teamId);
        if (team == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "队伍不存在");
        }
        return team;
    }

    /**
     * 根据队伍 id 获取队员信息
     *
     * @param teamId 队伍id
     * @return
     */
    private List<UserVO> getTeamMembers(Long teamId) {
        // 查出队伍中所有队员的 id
        QueryWrapper<TeamUser> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("team_id", teamId);
        List<Long> memberIdList = teamUserService.list(teamQueryWrapper)
                .stream().map(TeamUser::getUserId).toList();

        // 根据成员 id 查出成员信息0
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", memberIdList);
        return userService.listUserVO(userQueryWrapper);
    }


    /**
     * 查询某用户是否为指定队伍的队长
     *
     * @param team
     * @param userId
     */
    private boolean isCaptain(Team team,
                              Long userId) {
        return team.getUserId().equals(userId);
    }


}




