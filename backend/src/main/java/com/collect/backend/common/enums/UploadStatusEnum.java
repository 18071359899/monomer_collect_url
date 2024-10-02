package com.collect.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 上传文件状态枚举
 */
@Getter
@AllArgsConstructor
public enum UploadStatusEnum {
    UPLOAD_SECONDS("upload_seconds", "秒传"),
    UPLOADING("uploading", "上传中"),
    UPLOAD_SUCCESS("upload_finish", "上传成功");
    public static UploadStatusEnum getByType(String type){
        for (UploadStatusEnum constants : values()) {
            if (constants.getStatus().equals(type)) {
                return constants;
            }
        }
        return null;
    }
    /**
     * 分类
     */
    private final String status;
    /**
     * 描述
     */
    private final String desc;
}
