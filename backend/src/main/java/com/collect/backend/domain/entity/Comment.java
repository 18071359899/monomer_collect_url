package com.collect.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "内容不能为空")
    @Length(max = 1000,message = "名称不可以取太长哦，不然我记不住")
    private String content;
    @NotNull(message = "帖子不能为空")
    private Long shareId;
    @NotNull(message = "用户不能为空")
    private Long userId;
    @NotNull(message = "回复用户不能为空")
    private Long toUserId;
    @TableField("`agree`")
    private Integer agree;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date  createTime;
    @NotNull(message = "根评论不能为空")
    private Long rootId;
    @NotNull(message = "父评论不能为空")
    private Long fatherId;
}