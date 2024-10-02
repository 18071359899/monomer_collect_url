package com.collect.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 回收站表
 * @TableName recycle
 */
@TableName(value ="recycle")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recycle implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 删除的类型：0.网址  1.文件夹
     */
    private Integer type;

    /**
     * 删除Id
     */
    private Long deleteId;

    /**
     * 删除时间
     */
    @JsonFormat(timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm")
    private Date deleteTime;
}