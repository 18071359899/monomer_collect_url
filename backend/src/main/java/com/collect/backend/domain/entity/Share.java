package com.collect.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Share{
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "标题不能为空")
    @Length(max = 100,message = "标题不可以取太长哦，不然我记不住")
    private String title;
    private Long userId;
    @NotBlank(message = "内容不能为空")
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date updateTime;
    @TableField("`like`")
    private Integer like;    //点赞数
    @TableField("`comment`")
    private Integer comment;  //评论数
    @TableField("`collect`")
    private Integer collect;  //收藏数
    private Integer reading; //阅读量
}
