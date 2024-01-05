package com.pikachu.usercenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pikachu.usercenter.model.dto.request.TeamCreateRequest;
import com.pikachu.usercenter.model.dto.request.TeamJoinRequest;
import com.pikachu.usercenter.model.dto.request.TeamUpdateRequest;
import com.pikachu.usercenter.model.dto.response.BaseResponse;
import com.pikachu.usercenter.model.vo.TeamUserVO;
import com.pikachu.usercenter.service.TeamService;
import com.pikachu.usercenter.utils.ResultUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {
    @Resource
    private TeamService teamService;

    @PostMapping("/create")
    public BaseResponse<TeamUserVO> createTeam(@RequestBody @Valid TeamCreateRequest teamCreateRequest,
                                               HttpServletRequest request) {
        TeamUserVO newTeam = teamService.createTeam(teamCreateRequest, request);
        return ResultUtils.success(newTeam, "创建队伍成功");
    }

    @DeleteMapping("/dismiss")
    public BaseResponse<?> dismissTeam(@RequestParam Long teamId,
                                       HttpServletRequest request) {
        teamService.dismissTeam(teamId, request);
        return ResultUtils.success(true, "解散队伍成功");
    }

    @PutMapping("/update")
    public BaseResponse<TeamUserVO> updateTeam(@RequestBody @Valid TeamUpdateRequest teamUpdateRequest,
                                               HttpServletRequest request) {
        TeamUserVO team = teamService.updateTeam(teamUpdateRequest, request);
        return ResultUtils.success(team, "修改队伍信息成功");
    }

    @GetMapping("/get")
    public BaseResponse<TeamUserVO> getTeam(@RequestParam Long teamId) {
        TeamUserVO team = teamService.getTeamUserVOById(teamId);
        return ResultUtils.success(team, "获取队伍信息成功");
    }

    @GetMapping("/search")
    public BaseResponse<IPage<TeamUserVO>> searchTeam(@RequestParam(required = false) String keyword,
                                                      @RequestParam(defaultValue = "1") Long current,
                                                      @RequestParam(defaultValue = "5") Long size) {
        IPage<TeamUserVO> teamUserVOIPage = teamService.searchTeams(current, size, keyword);
        return ResultUtils.success(teamUserVOIPage);
    }

    @PostMapping("/join")
    public BaseResponse<?> joinTeam(@RequestBody @Valid TeamJoinRequest teamJoinRequest,
                                    HttpServletRequest request) {
        Long teamId = teamJoinRequest.getTeamId();
        String password = teamJoinRequest.getPassword();
        teamService.joinTeam(teamId, password, request);
        return ResultUtils.success(true, "加入队伍成功");
    }

    @DeleteMapping("/exit")
    public BaseResponse<?> exitTeam(@RequestParam Long teamId,
                                    HttpServletRequest request) {
        teamService.exitTeam(teamId, request);
        return ResultUtils.success(true, "退出队伍成功");
    }

}
