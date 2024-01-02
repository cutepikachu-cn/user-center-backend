package com.pikachu.usercenter.model.vo;

import com.pikachu.usercenter.model.entity.Team;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
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

    public static TeamUserVO combine(Team team, List<UserVO> members) throws InvocationTargetException, IllegalAccessException {
        TeamUserVO teamUserVO = new TeamUserVO();
        BeanUtils.copyProperties(teamUserVO, team);
        teamUserVO.setMembers(members);

        return teamUserVO;
    }
}
