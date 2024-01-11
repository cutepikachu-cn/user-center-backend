package com.pikachu.usercenter.common;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页请求参数封装类
 *
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Data
public class PageParams implements Serializable {
    @Serial
    private static final long serialVersionUID = -2541605382843900230L;
    @Range(min = 1)
    protected Long current = 1L;
    @Range(min = 1, max = 20, message = "每页最多20条")
    protected Long size = 5L;
}
