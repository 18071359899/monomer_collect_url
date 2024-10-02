package com.collect.backend.domain.vo.resp;

import lombok.Data;

/**
 * 分片上传文件成功后返回的信息
 */
@Data
public class UploadFileInfoResp {
        private String fileStatus; //文件上传状态
}
