package com.pikachu.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pikachu.usercenter.mapper.TeamUserMapper;
import com.pikachu.usercenter.model.entity.TeamUser;
import com.pikachu.usercenter.service.TeamUserService;
import org.springframework.stereotype.Service;

/**
 * @author 28944
 * @description 针对表【user_team(队伍~用户关系表)】的数据库操作Service实现
 * @createDate 2023-12-27 20:06:28
 */
@Service
public class TeamUserServiceImpl extends ServiceImpl<TeamUserMapper, TeamUser>
        implements TeamUserService {
}




