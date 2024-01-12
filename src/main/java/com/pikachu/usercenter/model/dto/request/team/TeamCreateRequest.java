package com.pikachu.usercenter.model.dto.request.team;

import com.pikachu.usercenter.model.entity.Team;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

/**
 * 创建队伍请求参数
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class TeamCreateRequest implements Serializable {

    public static Team toTeam(TeamCreateRequest teamCreateRequest) throws InvocationTargetException, IllegalAccessException {
        Team team = new Team();
        BeanUtils.copyProperties(team, teamCreateRequest);
        team.setCreateUserId(team.getUserId());

        return team;
    }

    @Serial
    private static final long serialVersionUID = 2441414884967522068L;

    @NotBlank(message = "队伍名称不能为空")
    @Length(min = 5, max = 20, message = "队伍名称长度为5~20")
    private String name;
    @Length(max = 512, message = "队伍长度为0~512")
    private String description;
    @Range(min = 2, max = 10, message = "队伍人数必须为2~10")
    private Integer maxNumber;
    private Date expireTime;
    private Long userId;
    @Range(min = 0, max = 2, message = "队伍状态（默认0 ~ 公开；1 ~ 私有；2 ~ 加密）")
    private Integer status;
    private String password;
    private List<String> tags;

    @AssertTrue(message = "加密队伍必须设置密码（8~20位字母、数字、下划线或减号组合）")
    public boolean hasPassword() {
        return status != 2 || (!StringUtils.isBlank(password) && password.matches("^[\\w-]{8,20}$"));
    }

    @AssertTrue(message = "过期时间至少为一天")
    public boolean isValidExpireTime() {
        return expireTime.after(DateUtils.addDays(new Date(), 1));
    }
}
