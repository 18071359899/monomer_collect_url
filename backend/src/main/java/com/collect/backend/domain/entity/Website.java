package com.collect.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName website
 */
@TableName(value ="website")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Website implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 网址url
     */
    @NotBlank(message = "url不能为空")
    @Length(max = 1000,message = "url不可以取太长哦，不然我记不住")
    private String url;

    /**
     * 网站标题
     */
    @Length(max = 300,message = "标题不可以取太长哦，不然我记不住")
    private String title;

    /**
     * 网站描述
     */
    @Length(max = 1000,message = "描述不可以取太长哦，不然我记不住")
    @TableField("`describe`")
    private String describe;

    /**
     * 网站图标
     */
    private String icon;

    /**
     * 父级标签，0表示根
     */
    @NotNull(message = "父级目录不能为空")
    private Long pid;

    /**
     * 
     */
    private Integer isDelete;

    /**
     * 
     */
    @JsonFormat(timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;

    /**
     * 
     */
    @JsonFormat(timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm")
    private Date updateTime;
    /**
     * 用户id
     */
    private Long  userId;
}