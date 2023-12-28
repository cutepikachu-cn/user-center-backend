package com.pikachu.usercenter.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pikachu.usercenter.common.ResponseCode;
import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.mapper.TeamMapper;
import com.pikachu.usercenter.model.dto.request.TeamCreateRequest;
import com.pikachu.usercenter.model.dto.request.TeamUpdateRequest;
import com.pikachu.usercenter.model.entity.Team;
import com.pikachu.usercenter.model.entity.UserTeam;
import com.pikachu.usercenter.model.enums.TeamStatus;
import com.pikachu.usercenter.model.vo.LoginUserVO;
import com.pikachu.usercenter.model.vo.TeamVO;
import com.pikachu.usercenter.service.TeamService;
import com.pikachu.usercenter.service.UserTeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

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

    @Override
    @Transactional
    public TeamVO createTeam(TeamCreateRequest teamCreateRequest, HttpServletRequest request) {
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

        return getTeamById(team.getId());
    }

    @Override
    @Transactional
    public boolean dismissTeam(Integer teamId, HttpServletRequest request) {
        Team team = getById(teamId);
        if (team == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "队伍不存在");
        }

        Long currentUserId = ((LoginUserVO) request.getSession().getAttribute(USER_LOGIN_STATE)).getId();
        if (!currentUserId.equals(team.getUserId())) {
            throw new BusinessException(ResponseCode.NO_AUTH);
        }

        return removeById(teamId);
    }

    @Override
    @Transactional
    public TeamVO updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        Team team = getById(teamUpdateRequest.getId());
        if (team == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "队伍不存在");
        }
        Long currentUserId = ((LoginUserVO) request.getSession().getAttribute(USER_LOGIN_STATE)).getId();
        if (!currentUserId.equals(team.getUserId())) {
            throw new BusinessException(ResponseCode.NO_AUTH);
        }

        BeanUtils.copyProperties(teamUpdateRequest, team);

        Integer status = team.getStatus();
        if (status != null) {
            TeamStatus teamStatus = TeamStatus.getEnumByValue(status);
            if (TeamStatus.SECRET.equals(teamStatus)) {
                String encryptPassword = DigestUtils.md5DigestAsHex((SALT + team.getPassword()).getBytes());
                team.setPassword(encryptPassword);
            }
        }

        if (!updateById(team)) {
            return null;
        }

        return TeamVO.fromTeam(getById(team.getId()));
    }

    @Override
    public TeamVO getTeamById(Long teamId) {
        if (teamId <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        return TeamVO.fromTeam(getById(teamId));
    }

    @Override
    public IPage<TeamVO> searchTeams(Long current, Long size) {
        Page<Team> teamPage = page(new Page<>(current, size));
        return teamPage.convert(TeamVO::fromTeam);
    }
}




