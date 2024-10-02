package com.collect.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String username;

    /**
     * 
     */
    private String password;

    /**
     * 
     */
    private String photo;
    @TableLogic
    private Integer isDelete;
    /**
     *
     */
    private Date createTime;
    /**
     * 关注数
     */
    private Integer follow;
    /**
     * 粉丝数
     */
    private Integer fans;
    /**
     * 简介
     */
    private String introduction;
    /**
     * 更新时间
     */
    private Date updateTime;
//    /**
//     * 用户上传文件总空间
//     */
//    private Long userFileTotalSpace;
//    /**
//     * 用户已使用上传文件空间
//     */
//    private Long userFileUseSpace;
}