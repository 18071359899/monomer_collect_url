package com.collect.backend.domain.vo.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 存储到redis的数据对象：用户上传文件的总空间和使用空间大小
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSpaceRedisVo {
    /**
     * 用户上传文件总空间
     */
    private Long userFileTotalSpace;
    /**
     * 用户已使用上传文件空间
     */
    private Long userFileUseSpace;
    /**
     * 上传的分片信息，防止断网重新上传时在redis计算出现重复计算问题
     */
    private List<String> chunkFileInfo;  //分片信息格式：md5值+分片的序号
}
