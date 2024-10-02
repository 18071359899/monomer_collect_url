package com.collect.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * 用户使用空间、总空间
 * @TableName user_use_total
 */
@TableName(value ="user_use_total")
@AllArgsConstructor
public class UserUseTotal implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户使用空间
     */
    private Long userUse;

    /**
     * 用户总空间
     */
    private Long userTotal;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    public Long getId() {
        return id;
    }

    /**
     * id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 用户id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 用户id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 用户使用空间
     */
    public Long getUserUse() {
        return userUse;
    }

    /**
     * 用户使用空间
     */
    public void setUserUse(Long userUse) {
        this.userUse = userUse;
    }

    /**
     * 用户总空间
     */
    public Long getUserTotal() {
        return userTotal;
    }

    /**
     * 用户总空间
     */
    public void setUserTotal(Long userTotal) {
        this.userTotal = userTotal;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", userUse=").append(userUse);
        sb.append(", userTotal=").append(userTotal);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}