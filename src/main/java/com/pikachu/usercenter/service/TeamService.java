package com.pikachu.usercenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pikachu.usercenter.model.dto.request.TeamCreateRequest;
import com.pikachu.usercenter.model.dto.request.TeamUpdateRequest;
import com.pikachu.usercenter.model.entity.Team;
import com.pikachu.usercenter.model.vo.TeamUserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 28944
 * @description 针对表【team(队伍表)】的数据库操作Service
 * @createDate 2023-12-27 20:04:43
 */
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param teamCreateRequest 创建队伍请求参数
     * @param request
     * @return 新注册的队伍信息对象
     */
    @Transactional
    TeamUserVO createTeam(TeamCreateRequest teamCreateRequest, HttpServletRequest request);

    /**
     * 解散队伍
     *
     * @param teamId  队伍id
     * @param request
     */
    @Transactional
    void dismissTeam(Long teamId, HttpServletRequest request);

    /**
     * 更新队伍信息
     *
     * @param teamUpdateRequest 更新队伍信息底箱
     * @param request
     * @return 更新后的队伍信息对象
     */
    @Transactional
    TeamUserVO updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request);

    /**
     * 更具队伍id获取队伍信息
     *
     * @param teamId 队伍id
     * @return 队伍信息对象
     */
    TeamUserVO getTeamUserVOById(Long teamId);

    /**
     * 搜索队伍
     *
     * @param current 页数
     * @param size    每页记录数
     * @param keyword 搜索关键词
     * @return 搜索到的队伍信息分页对象
     */
    IPage<TeamUserVO> searchTeams(Long current, Long size, String keyword);

    /**
     * 加入队伍
     *
     * @param teamId   队伍id
     * @param password 加密队伍密码
     * @param request
     */
    @Transactional
    void joinTeam(Long teamId, String password, HttpServletRequest request);

    /**
     * 退出队伍
     *
     * @param teamId  队伍id
     * @param request
     */
    @Transactional
    void exitTeam(Long teamId, HttpServletRequest request);

    /**
     * 转让队伍
     *
     * @param teamId  队伍id
     * @param userId  受让用户id
     * @param request
     */
    @Transactional
    void transferTeam(Long teamId, Long userId, HttpServletRequest request);

    /**
     * 获取当前用户管理（为队长）的队伍信息列表
     *
     * @param request
     * @return
     */
    List<TeamUserVO> getManageTeams(HttpServletRequest request);

    /**
     * 获取当前用户加入的队伍信息列表
     *
     * @param request
     * @return
     */
    List<TeamUserVO> getJoinedTeams(HttpServletRequest request);

}
