package com.pikachu.usercenter.model.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class TeamCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 2441414884967522068L;

    @NotBlank(message = "队伍名称不能为空")
    private String name;
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

    @AssertTrue(message = "过期时间不能早于当前时间")
    public boolean isValidExpireTime() {
        return expireTime.after(new Date());
    }
}
