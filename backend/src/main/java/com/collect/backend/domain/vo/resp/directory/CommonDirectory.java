package com.collect.backend.domain.vo.resp.directory;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class CommonDirectory {
    private Long id;
    /**
     * 父id
     */
    private Long pid;
    /**
     * 目录名称或文件名称，根据type来决定
     */
    private String name;
    /**
     * 网址url
     */
    private String url;
    /**
     * 网站标题
     */
    private String title;
    /**
     * 网站描述
     */
    private String describe;
    /**
     * 网站图标或文件缩略图，根据type而定
     */
    private String icon;
    /**
     * 目录分类： 0，文件夹  1，网址，2上传的文件
     */
    private Integer type;
    /**
     * 上传的文件地址链接，当type为2时可用
     */
    private String filePath;
    /**
     * 视频下载地址，type为2，fileType为0时生效
     */
    private String videoFilePath;
    /**
     * 上传的文件类型：0：视频，1：图片，2：文档，3：音频，4：其他
     * 当type为2时有效
     */
    private Integer fileType;
    @JsonFormat(timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm")
    private Date updateTime;
}
