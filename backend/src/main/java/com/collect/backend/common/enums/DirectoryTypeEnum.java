package com.collect.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 目录分类： 0，文件夹  1，网址
 */
@Getter
@AllArgsConstructor
public enum DirectoryTypeEnum {
    CATEGORY(0, "文件夹"),
    WEBSITE(1, "网址"),
    UPLOAD_FILE(2,"上传的文件");
    public static DirectoryTypeEnum getByType(int type){
        for (DirectoryTypeEnum constants : values()) {
            if (constants.getType() == type) {
                return constants;
            }
        }
        return null;
    }
    /**
     * 分类
     */
    private final Integer type;
    /**
     * 描述
     */
    private final String desc;
}
