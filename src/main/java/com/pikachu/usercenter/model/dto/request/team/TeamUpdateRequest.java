package com.pikachu.usercenter.model.dto.request.team;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.lang.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 更新队伍信息请求餐宿
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class TeamUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 8319647431623571367L;

    @NotNull(message = "队伍id不能为空")
    private Long id;
    @Length(min = 5, max = 20, message = "队伍名称长度为5~20")
    private String name;
    @Length(max = 512, message = "队伍长度为0~512")
    private String description;
    @Nullable
    @Range(min = 2, max = 10, message = "队伍人数必须为2~10")
    private Integer maxNumber;
    private Date expireTime;
    private Long userId;
    @Nullable
    @Range(min = 0, max = 2, message = "队伍状态（默认0 ~ 公开；1 ~ 私有；2 ~ 加密）")
    private Integer status;
    private String password;
    private List<String> tags;

    @AssertTrue(message = "加密队伍必须设置密码（8~20位字母、数字、下划线或减号组合）")
    public boolean hasPassword() {
        return status == null || status != 2 || (!StringUtils.isBlank(password) && password.matches("^[\\w-]{8,20}$"));
    }

    @AssertTrue(message = "过期时间不能早于当前时间")
    public boolean isValidExpireTime() {
        return expireTime == null || expireTime.after(new Date());
    }
}
