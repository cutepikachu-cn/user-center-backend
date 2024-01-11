package com.pikachu.usercenter.model.enums;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 队伍状态枚举类
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum TeamStatus {
    PUBLIC(0, "公开"),
    PRIVATE(1, "私密"),
    SECRET(2, "加密");
    private final Integer value;
    private final String description;

    public static TeamStatus getEnumByValue(@NotNull Integer value) {
        for (TeamStatus teamStatus : TeamStatus.values()) {
            if (teamStatus.getValue().equals(value)) {
                return teamStatus;
            }
        }
        return null;
    }

}
