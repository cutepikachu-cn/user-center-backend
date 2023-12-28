package com.pikachu.usercenter.model.vo;

import com.pikachu.usercenter.model.entity.Team;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍视图对象
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class TeamVO implements Serializable {

    public static TeamVO fromTeam(Team team) {
        if (team == null)
            return null;
        TeamVO teamVO = new TeamVO();
        BeanUtils.copyProperties(team, teamVO);
        return teamVO;
    }

    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 队伍最大人数
     */
    private Integer maxNumber;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 队长id
     */
    private Long userId;

    /**
     * 队伍状态（默认0 ~ 公开；1 ~ 私有；2 ~ 加密）
     */
    private Integer status;

    /**
     * 用户标签列表，以`,`分割
     */
    private List<String> tags;

    /**
     * 创建时间
     */
    private Date createTime;

    @Serial
    private static final long serialVersionUID = 1L;
}

