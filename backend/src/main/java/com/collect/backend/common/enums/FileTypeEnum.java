package com.collect.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件类型枚举
 */
@Getter
@AllArgsConstructor
public enum FileTypeEnum {
    MOVIE_TYPE(0,"视频"),
    IMG_TYPE(1,"图片"),
    DOCUMENT_TYPE(2,"文档"),
    AUDIO_TYPE(3,"音频"),
    OTHER_TYPE(4,"其他类型");
    public static FileTypeEnum getByType(int type){
        for (FileTypeEnum constants : values()) {
            if (constants.getType() == type) {
                return constants;
            }
        }
        return null;
    }

    private Integer type;
    private String  desc;
}
