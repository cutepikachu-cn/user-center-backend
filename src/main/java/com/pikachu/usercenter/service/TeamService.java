package com.pikachu.usercenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pikachu.usercenter.model.dto.request.TeamCreateRequest;
import com.pikachu.usercenter.model.dto.request.TeamUpdateRequest;
import com.pikachu.usercenter.model.entity.Team;
import com.pikachu.usercenter.model.vo.TeamVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author 28944
 * @description 针对表【team(队伍表)】的数据库操作Service
 * @createDate 2023-12-27 20:04:43
 */
public interface TeamService extends IService<Team> {

    TeamVO createTeam(TeamCreateRequest teamCreateRequest, HttpServletRequest request);

    void dismissTeam(Long teamId, HttpServletRequest request);

    TeamVO updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request);

    TeamVO getTeamVOById(Long teamId);

    IPage<TeamVO> searchTeams(Long current, Long size);
}
