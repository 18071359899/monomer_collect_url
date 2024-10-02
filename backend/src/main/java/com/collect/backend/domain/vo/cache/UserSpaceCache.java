package com.collect.backend.domain.vo.cache;

import lombok.Data;

@Data
public class UserSpaceCache {
    /**
     * 用户上传文件总空间
     */
    private String userFileTotalSpace;
    /**
     * 用户已使用上传文件空间
     */
    private String userFileUseSpace;
    private Double userFileTotalSpaceValue;
    private Double userFileUseSpaceValue;
}
