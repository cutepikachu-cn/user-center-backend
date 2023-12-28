package com.pikachu.usercenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pikachu.usercenter.common.ResponseCode;
import com.pikachu.usercenter.exception.BusinessException;
import com.pikachu.usercenter.model.dto.request.TeamCreateRequest;
import com.pikachu.usercenter.model.dto.request.TeamUpdateRequest;
import com.pikachu.usercenter.model.dto.response.BaseResponse;
import com.pikachu.usercenter.model.vo.TeamVO;
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
    public BaseResponse<TeamVO> createTeam(@RequestBody @Valid TeamCreateRequest teamCreateRequest,
                                           HttpServletRequest request) {
        TeamVO newTeam = teamService.createTeam(teamCreateRequest, request);
        if (newTeam == null) {
            return ResultUtils.error(ResponseCode.SYSTEM_ERROR, "创建队伍失败");
        }
        return ResultUtils.success(newTeam, "创建队伍成功");
    }

    @DeleteMapping("/dismiss")
    public BaseResponse<?> dismissTeam(@RequestParam Integer teamId,
                                       HttpServletRequest request) {
        if (!teamService.dismissTeam(teamId, request)) {
            return ResultUtils.error(ResponseCode.SYSTEM_ERROR, "解散队伍失败");
        }
        return ResultUtils.success("解散队伍成功");
    }

    @PutMapping("/update")
    public BaseResponse<?> updateTeam(@RequestBody @Valid TeamUpdateRequest teamUpdateRequest,
                                      HttpServletRequest request) {
        TeamVO teamVO = teamService.updateTeam(teamUpdateRequest, request);
        if (teamVO == null) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "修改队伍信息失败");
        }
        return ResultUtils.success(teamVO, "修改队伍信息成功");
    }

    @GetMapping("/get")
    public BaseResponse<TeamVO> getTeam(@RequestParam Integer teamId) {
        TeamVO teamVO = teamService.getTeamById(teamId);
        if (teamVO == null) {
            return ResultUtils.error(ResponseCode.PARAMS_ERROR, "获取队伍信息失败");
        }
        return ResultUtils.success(teamVO, "获取队伍信息成功");
    }

    @GetMapping("/search")
    public BaseResponse<IPage<TeamVO>> search(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "5") Integer size) {
        IPage<TeamVO> teamVOIPage = teamService.searchTeams(current, size);
        return ResultUtils.success(teamVOIPage);
    }

}
