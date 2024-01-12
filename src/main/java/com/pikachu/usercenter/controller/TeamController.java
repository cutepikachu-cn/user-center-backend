package com.pikachu.usercenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pikachu.usercenter.model.dto.request.team.TeamCreateRequest;
import com.pikachu.usercenter.model.dto.request.team.TeamJoinRequest;
import com.pikachu.usercenter.model.dto.request.team.TeamUpdateRequest;
import com.pikachu.usercenter.model.dto.response.BaseResponse;
import com.pikachu.usercenter.model.vo.TeamUserVO;
import com.pikachu.usercenter.service.TeamService;
import com.pikachu.usercenter.utils.ResultUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 创建队伍
     *
     * @param teamCreateRequest 创建队伍请求参数对象
     * @param request
     * @return
     */
    @PostMapping("/create")
    public BaseResponse<TeamUserVO> createTeam(@RequestBody @Valid TeamCreateRequest teamCreateRequest,
                                               HttpServletRequest request) {
        TeamUserVO newTeam = teamService.createTeam(teamCreateRequest, request);
        return ResultUtils.success(newTeam, "创建队伍成功");
    }

    /**
     * 解散队伍
     *
     * @param teamId  队伍id
     * @param request
     * @return
     */
    @DeleteMapping("/dismiss")
    public BaseResponse<?> dismissTeam(@RequestParam Long teamId,
                                       HttpServletRequest request) {
        teamService.dismissTeam(teamId, request);
        return ResultUtils.success(true, "解散队伍成功");
    }

    /**
     * 更新队伍信息
     *
     * @param teamUpdateRequest 更新队伍信息请求对象
     * @param request
     * @return
     */
    @PutMapping("/update")
    public BaseResponse<TeamUserVO> updateTeam(@RequestBody @Valid TeamUpdateRequest teamUpdateRequest,
                                               HttpServletRequest request) {
        TeamUserVO team = teamService.updateTeam(teamUpdateRequest, request);
        return ResultUtils.success(team, "修改队伍信息成功");
    }

    /**
     * 获取指定队伍信息
     *
     * @param teamId 队伍id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<TeamUserVO> getTeam(@RequestParam Long teamId) {
        TeamUserVO team = teamService.getTeamUserVOById(teamId);
        return ResultUtils.success(team, "获取队伍信息成功");
    }

    /**
     * 搜索队伍
     * @param keyword 搜索关键词
     * @param current 页数
     * @param size 每页记录数
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<IPage<TeamUserVO>> searchTeam(@RequestParam(required = false) String keyword,
                                                      @RequestParam(defaultValue = "1") Long current,
                                                      @RequestParam(defaultValue = "5") Long size) {
        IPage<TeamUserVO> teamUserVOIPage = teamService.searchTeams(current, size, keyword);
        return ResultUtils.success(teamUserVOIPage);
    }

    /**
     * 加入队伍
     * @param teamJoinRequest 加入队伍请求参数对象
     * @param request
     * @return
     */
    @PostMapping("/join")
    public BaseResponse<?> joinTeam(@RequestBody @Valid TeamJoinRequest teamJoinRequest,
                                    HttpServletRequest request) {
        Long teamId = teamJoinRequest.getTeamId();
        String password = teamJoinRequest.getPassword();
        teamService.joinTeam(teamId, password, request);
        return ResultUtils.success(true, "加入队伍成功");
    }

    /**
     * 退出队伍
     * @param teamId 队伍id
     * @param request
     * @return
     */
    @DeleteMapping("/exit")
    public BaseResponse<?> exitTeam(@RequestParam Long teamId,
                                    HttpServletRequest request) {
        teamService.exitTeam(teamId, request);
        return ResultUtils.success(true, "退出队伍成功");
    }

    /**
     * 转让队长
     * @param teamId 队伍id
     * @param userId 转让接受方用户id
     * @param request
     * @return
     */
    @PostMapping("/transfer")
    public BaseResponse<?> transferTeam(@RequestParam Long teamId,
                                        @RequestParam Long userId,
                                        HttpServletRequest request) {
        teamService.transferTeam(teamId, userId, request);
        return ResultUtils.success(true, "转让队伍成功");
    }

    /**
     * 获取当前用户管理（为队长）的队伍
     * @param request
     * @return
     */
    @GetMapping("/my/manage")
    public BaseResponse<List<TeamUserVO>> listMyManageTeams(HttpServletRequest request) {
        List<TeamUserVO> manageTeams = teamService.getManageTeams(request);
        return ResultUtils.success(manageTeams);
    }

    /**
     * 获取用户加入的队伍
     * @param request
     * @return
     */
    @GetMapping("/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinedTeams(HttpServletRequest request) {
        List<TeamUserVO> joinedTeams = teamService.getJoinedTeams(request);
        return ResultUtils.success(joinedTeams);
    }

}
