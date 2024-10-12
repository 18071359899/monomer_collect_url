package com.collect.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户点赞计数表
 * @TableName user_agree_share
 */
@TableName(value ="user_agree_share")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class UserAgreeShare implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long userAgreeShareId;

    /**
     * 文章id
     */
    private Long shareId;

    /**
     * 点赞总数
     */
    private Long cnt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}