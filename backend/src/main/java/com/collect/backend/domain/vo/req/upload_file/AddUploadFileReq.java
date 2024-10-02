package com.collect.backend.domain.vo.req.upload_file;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AddUploadFileReq{
    @NotBlank(message = "文件夹名称不能为空")
    @Size(max = 30,message = "文件夹名称不能超过30个字符")
    private String monthDay;
    @NotBlank(message = "文件名不能为空")
    @Size(max = 100,message = "文件名不能超过100个字符")
    private String realFileName;
    /**
     * 防重复
     */
    @NotBlank(message = "md5值不能为空")
    @Size(max = 300,message = "md5值不能超过300个字符")
    private String md5;
    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空")
    @Size(max = 500,message = "md5值不能超过500个字符")
    private String fileName;
    /**
     * 父级目录id
     */
    @NotNull(message = "父级目录编号不能为空")
    private Long pid;
    /**
     * 文件总大小
     */
    @NotNull(message = "文件总大小不能为空")
    private Long fileSize;
}
