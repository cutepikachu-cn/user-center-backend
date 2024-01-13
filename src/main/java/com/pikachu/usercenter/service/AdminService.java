package com.pikachu.usercenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pikachu.usercenter.model.dto.request.admin.TeamUpdateRequestAdmin;
import com.pikachu.usercenter.model.dto.request.admin.UserUpdateRequestAdmin;
import com.pikachu.usercenter.model.entity.Team;
import com.pikachu.usercenter.model.entity.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理员服务
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
public interface AdminService {
    /**
     * 查询用户列表
     *
     * @param current 页码
     * @param size    每页条数
     * @return 查询到的用户页
     */
    IPage<User> listUser(Long current, Long size);

    /**
     * 查询队伍列表
     *
     * @param current 页码
     * @param size    每页记录数
     * @return 拆卸拿到的队伍页
     */
    IPage<Team> listTeam(Long current, Long size);

    @Transactional
    User updateUser(UserUpdateRequestAdmin params);

    @Transactional
    Team updateTeam(TeamUpdateRequestAdmin params);

    @Transactional
    void deleteUser(Long userId);

    @Transactional
    void deleteTeam(Long TeamId);
}
