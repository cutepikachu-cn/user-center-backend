package com.pikachu.usercenter.model.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 加入队伍请求参数
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class TeamJoinRequest {
    @Min(1)
    private Long teamId;
    private String password;
}
