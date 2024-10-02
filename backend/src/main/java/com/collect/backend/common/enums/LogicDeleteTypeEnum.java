package com.collect.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 逻辑删除枚举
 */
@Getter
@AllArgsConstructor
public enum LogicDeleteTypeEnum {
    NO_DELETE(0,"未删除"),
    YES_DELETE(1,"已删除，进入回收站");
    public static LogicDeleteTypeEnum getByType(int type){
        for (LogicDeleteTypeEnum constants : values()) {
            if (constants.getType() == type) {
                return constants;
            }
        }
        return null;
    }

    private Integer type;
    private String  desc;
}
