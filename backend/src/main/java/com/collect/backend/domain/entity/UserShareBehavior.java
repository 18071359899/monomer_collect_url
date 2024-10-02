package com.collect.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户对帖子的行为：点赞，收藏
 * @TableName user_share_behavior
 */
@TableName(value ="user_share_behavior")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShareBehavior implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *  
     */
    private Long userId;

    /**
     * 行为类型：0，用户点赞   1，用户收藏
     */
    private Integer type;
    /**
     * 
     */
    private Date createTime;
    /**
     *  用户行为的关联表id，根据类型决定
     */
    private Long relationId;
}