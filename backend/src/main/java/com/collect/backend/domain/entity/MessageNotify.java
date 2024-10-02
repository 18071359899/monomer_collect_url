package com.collect.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户消息通知表
 * @TableName message_notify
 */
@TableName(value ="message_notify")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageNotify implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 接受用户id
     */
    private Long acceptUserId;
    /**
     * 发送用户id
     */
    private Long sendUserId;
    /**
     * 消息类型：0，评论回复，1：点赞，2：收藏，3：关注
     */
    private Integer type;
    /**
     * 业务id，比如评论的具体id
     */
    private Long businessId;
    /**
     * 用户是否阅读，false表示否，true表示是
     */
    private Boolean isRead;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}