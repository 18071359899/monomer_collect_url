package com.collect.backend.domain.vo.req;

import lombok.Data;

@Data
public class UnionFileReq {
    private String fileName;
    private String fileMd5;
    private Integer chunkCount; //总分片
}
