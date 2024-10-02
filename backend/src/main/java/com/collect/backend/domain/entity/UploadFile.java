package com.collect.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 文件上传表
 * @TableName file
 */
@TableName(value ="upload_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFile implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 防重复
     */
    private String md5;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空")
    @Size(max = 500,message = "md5值不能超过500个字符")
    private String fileName;
    /**
     * 文件类型：0：视频，1：图片，2：文档，3：音频，4：其他
     */
    private Integer fileType;
    /**
     * 父级目录id
     */
    @NotNull(message = "父级目录编号不能为空")
    private Long pid;
    /**
     * 缩略图路径
     */
    private String thumbnail;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    private Integer isDelete;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}