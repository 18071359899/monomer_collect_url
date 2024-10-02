package com.collect.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对用户行为的添加或删除
 */
@Getter
@AllArgsConstructor
public enum UserShareBehaviorISAddType {
    ADD_TYPE(1, "添加该行为"),
    DELETE_TYPE(0, "删除该行为");
    public static UserShareBehaviorISAddType getByType(int type){
        for (UserShareBehaviorISAddType constants : values()) {
            if (constants.getType() == type) {
                return constants;
            }
        }
        return null;
    }
    /**
     * 用户行为的类型
     */
    private final int type;

    /**
     * 描述
     */
    private final String desc;
}
