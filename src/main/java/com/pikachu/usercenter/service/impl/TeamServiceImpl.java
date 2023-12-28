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
import com.pikachu.usercenter.model.vo.LoginUserVO;
import com.pikachu.usercenter.model.vo.TeamVO;
import com.pikachu.usercenter.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
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

    @Override
    public TeamVO createTeam(TeamCreateRequest teamCreateRequest, HttpServletRequest request) {
        Team team = new Team();
        BeanUtils.copyProperties(teamCreateRequest, team);
        Long currentUserId = ((LoginUserVO) request.getSession().getAttribute(USER_LOGIN_STATE)).getId();
        team.setUserId(currentUserId);

        if (team.getStatus() == 2) {
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + team.getPassword()).getBytes());
            team.setPassword(encryptPassword);
        }

        if (!save(team)) {
            return null;
        }

        return TeamVO.fromTeam(getById(team.getId()));
    }

    @Override
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
        if (team.getStatus() != null && team.getStatus() == 2) {
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + team.getPassword()).getBytes());
            team.setPassword(encryptPassword);
        }

        if (!updateById(team)) {
            return null;
        }

        return TeamVO.fromTeam(getById(team.getId()));
    }

    @Override
    public TeamVO getTeamById(Integer teamId) {
        if (teamId <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        return TeamVO.fromTeam(getById(teamId));
    }

    @Override
    public IPage<TeamVO> searchTeams(Integer current, Integer size) {
        Page<Team> teamPage = page(new Page<>(current, size));
        return teamPage.convert(TeamVO::fromTeam);
    }
}




