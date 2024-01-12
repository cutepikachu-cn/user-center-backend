package com.pikachu.usercenter.model.dto.request.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 更新用户信息请求参数
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class UserUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 5218331691290637054L;

    private Long id;
    @Length(max = 30, message = "昵称长度必须在0~30")
    private String nickname;
    @Length(max = 1024, message = "头像URL长度必须在0~1024")
    private String avatarUrl;
    @Length(max = 512, message = "个人介绍长度必须在0~512")
    private String profile;
    private Boolean gender;
    @Range(min = 1, max = 200, message = "年龄必须在1~200")
    private Integer age;
    @Length(min = 5, max = 20, message = "手机号长度必须在5~20之间")
    private String phone;
    @Length(min = 5, max = 64, message = "邮箱长度必须在5~64之间")
    private String email;
    private List<String> tags;
}
