package com.collect.backend.domain.entity.mq_event;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.collect.backend.domain.entity.UserShareBehavior;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@TableName(value ="user_share_behavior")
public class LikeQueueEntity extends UserShareBehavior {
    /**
     * 增加或减少 ： 0 减少  1 增加
     */
    @NotNull(message = "业务类型不能为空")
    @TableField(exist = false)
    private Integer isAdd;
}
