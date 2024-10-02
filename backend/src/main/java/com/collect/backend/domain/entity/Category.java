package com.collect.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 给网站做分类的表
 * @TableName category
 */
@TableName(value ="category")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Category implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父id
     */
    @NotNull(message = "父级目录不能为空")
    private Long pid;

    /**
     * 分类名称
     */
    @NotBlank(message = "名称不能为空")
    @Length(max = 100,message = "名称不可以取太长哦，不然我记不住")
    private String name;
    /**
     * 用户id
     */
    private Long userId;

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
}