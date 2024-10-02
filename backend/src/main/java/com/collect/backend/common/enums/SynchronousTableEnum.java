package com.collect.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 同步表枚举
 */
@Getter
@AllArgsConstructor
public enum SynchronousTableEnum { 	//记录要进行同步的表
    WEBSITE("website","网址表"),
    CATEGORY("category","目录表"),
    USER("user","用户表"),
    SHARE("share","分享表"),
    UPLOAD_FILE("upload_file","上传文件表");
    public static SynchronousTableEnum getByTableName(String tableName){
        for (SynchronousTableEnum constants : values()) {
            if (constants.getTableName().equals(tableName)) {
                return constants;
            }
        }
        return null;
    }
    private String tableName;
    private String desc;
}
