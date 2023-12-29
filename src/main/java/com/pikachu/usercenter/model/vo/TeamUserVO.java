package com.pikachu.usercenter.model.vo;

import com.pikachu.usercenter.model.entity.Team;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍及成员信息
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class TeamUserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1546146245884100023L;

    private Long id;
    private String name;
    private String description;
    private Integer maxNumber;
    private Date expireTime;
    private Long userId;
    private Integer status;
    private List<String> tags;
    private Date createTime;

    /**
     * 成员信息
     */
    private List<UserVO> members;

    public static TeamUserVO combine(Team team, List<UserVO> members) {
        TeamUserVO teamUserVO = new TeamUserVO();
        BeanUtils.copyProperties(team, teamUserVO);
        teamUserVO.setMembers(members);

        return teamUserVO;
    }
}
