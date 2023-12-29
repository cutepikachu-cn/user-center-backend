package com.pikachu.usercenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pikachu.usercenter.model.dto.request.TeamCreateRequest;
import com.pikachu.usercenter.model.dto.request.TeamUpdateRequest;
import com.pikachu.usercenter.model.entity.Team;
import com.pikachu.usercenter.model.vo.TeamUserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author 28944
 * @description 针对表【team(队伍表)】的数据库操作Service
 * @createDate 2023-12-27 20:04:43
 */
public interface TeamService extends IService<Team> {

    TeamUserVO createTeam(TeamCreateRequest teamCreateRequest, HttpServletRequest request);

    void dismissTeam(Long teamId, HttpServletRequest request);

    TeamUserVO updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request);

    TeamUserVO getTeamUserVOById(Long teamId);

    IPage<TeamUserVO> searchTeams(Long current, Long size, String keyword);

    void joinTeam(Long teamId, String password, HttpServletRequest request);
}
