package com.pikachu.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pikachu.usercenter.common.ResponseCode;
import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.mapper.TeamMapper;
import com.pikachu.usercenter.model.dto.request.TeamCreateRequest;
import com.pikachu.usercenter.model.dto.request.TeamUpdateRequest;
import com.pikachu.usercenter.model.entity.Team;
import com.pikachu.usercenter.model.entity.User;
import com.pikachu.usercenter.model.entity.UserTeam;
import com.pikachu.usercenter.model.enums.TeamStatus;
import com.pikachu.usercenter.model.vo.LoginUserVO;
import com.pikachu.usercenter.model.vo.TeamUserVO;
import com.pikachu.usercenter.model.vo.UserVO;
import com.pikachu.usercenter.service.TeamService;
import com.pikachu.usercenter.service.UserService;
import com.pikachu.usercenter.service.UserTeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

import static com.pikachu.usercenter.constant.UserConstant.USER_LOGIN_STATE;
import static com.pikachu.usercenter.service.UserService.SALT;

/**
 * @author 28944
 * @description 针对表【team(队伍表)】的数据库操作Service实现
 * @createDate 2023-12-27 20:04:43
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    UserTeamService userTeamService;

    @Resource
    UserService userService;

    @Override
    @Transactional
    public TeamUserVO createTeam(TeamCreateRequest teamCreateRequest, HttpServletRequest request) {
        // 创建队伍对象
        Team team = new Team();
        BeanUtils.copyProperties(teamCreateRequest, team);
        Long currentUserId = ((LoginUserVO) request.getSession().getAttribute(USER_LOGIN_STATE)).getId();
        // 队伍为当前登录的用户创建的
        team.setUserId(currentUserId);

        // 是否为私密队伍
        TeamStatus teamStatus = TeamStatus.getEnumByValue(team.getStatus());
        if (TeamStatus.SECRET.equals(teamStatus)) {
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + team.getPassword()).getBytes());
            team.setPassword(encryptPassword);
        }

        // 存储队伍信息
        if (!save(team)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "创建队伍失败");
        }

        // 添加队伍~队员关系
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(team.getId());
        userTeam.setUserId(currentUserId);
        if (!userTeamService.save(userTeam)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "创建队伍失败");
        }

        return getTeamVOById(team.getId());
    }

    @Override
    @Transactional
    public void dismissTeam(Long teamId, HttpServletRequest request) {
        // 获取要解散的队伍
        Team team = getById(teamId);
        if (team == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "队伍不存在");
        }

        // 是否有权解散
        // 只能解散自己的队伍
        checkActionAuth(team.getUserId(), request);

        // 解散队伍
        if (!removeById(teamId)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "解散队伍失败");
        }

        // 删除用户~队伍关系表中的关系数据
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id", teamId);
        if (!userTeamService.remove(queryWrapper)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "解散队伍失败");
        }

    }

    @Override
    @Transactional
    public TeamUserVO updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        // 要修改信息的队伍是否存在
        Team team = getById(teamUpdateRequest.getId());
        if (team == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "队伍不存在");
        }

        // 检查是否为自己的队伍
        checkActionAuth(team.getUserId(), request);

        // 拷贝要修改的信息
        BeanUtils.copyProperties(teamUpdateRequest, team);

        // 是否设置为了加密队伍
        Integer status = team.getStatus();
        if (status != null) {
            TeamStatus teamStatus = TeamStatus.getEnumByValue(status);
            if (TeamStatus.SECRET.equals(teamStatus)) {
                String encryptPassword = DigestUtils.md5DigestAsHex((SALT + team.getPassword()).getBytes());
                team.setPassword(encryptPassword);
            }
        }

        // 跟新队伍信息
        if (!updateById(team)) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "修改队伍信息失败");
        }

        // 返回修改后的队伍信息
        return getTeamVOById(team.getId());
    }

    @Override
    public TeamUserVO getTeamVOById(Long teamId) {
        if (teamId <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        Team team = getById(teamId);
        if (team == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "队伍不存在");
        }

        List<UserVO> teamMembers = getTeamMembers(teamId);
        TeamUserVO teamUserVO = TeamUserVO.combine(team, teamMembers);

        return teamUserVO;
    }

    private List<UserVO> getTeamMembers(Long teamId) {
        // 查出队伍中所有队员的 id
        QueryWrapper<UserTeam> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("team_id", teamId);
        List<Long> memberIdList = userTeamService.list(teamQueryWrapper)
                .stream().map(UserTeam::getUserId).toList();

        // 根据成员 id 查出成员信息0
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", memberIdList);
        return userService.listUserVO(userQueryWrapper);
    }

    @Override
    public IPage<TeamUserVO> searchTeams(Long current, Long size, String keyword) {
        LambdaQueryWrapper<Team> teamLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 筛除私密 / 加密队伍
        teamLambdaQueryWrapper.notIn(Team::getStatus, 1, 2);
        // 筛除已过期队伍
        teamLambdaQueryWrapper.lt(Team::getExpireTime, new Date());

        if (!StringUtils.isBlank(keyword)) {
            teamLambdaQueryWrapper.or(qr -> qr.like(Team::getName, keyword))
                    .or(qr -> qr.like(Team::getDescription, keyword))
                    .or(qr -> qr.like(Team::getTags, keyword));
        }

        Page<Team> teamPage = page(new Page<>(current, size), teamLambdaQueryWrapper);
        IPage<TeamUserVO> teamUserVOIPage = teamPage.convert(team -> getTeamVOById(team.getId()));
        return teamUserVOIPage;
    }

    /**
     * 检查进行操作的队伍是否为当前用户的队伍
     *
     * @param teamUserId 队伍 id
     * @param request
     */
    private void checkActionAuth(Long teamUserId,
                                 HttpServletRequest request) {
        Long currentUserId = ((LoginUserVO) request.getSession().getAttribute(USER_LOGIN_STATE)).getId();
        if (!currentUserId.equals(teamUserId))
            throw new BusinessException(ResponseCode.NO_AUTH);
    }
}




