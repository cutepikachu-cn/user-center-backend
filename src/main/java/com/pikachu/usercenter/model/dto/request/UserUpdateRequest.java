package com.pikachu.usercenter.model.dto.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class UserUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 5218331691290637054L;

    private Long id;
    private String nickname;
    private String avatarUrl;
    private String profile;
    private Boolean gender;
    private Integer age;
    private String phone;
    private String email;
    private List<String> tags;
}
