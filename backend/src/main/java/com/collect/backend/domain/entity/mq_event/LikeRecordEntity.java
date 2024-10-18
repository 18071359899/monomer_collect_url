package com.collect.backend.domain.entity.mq_event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 点赞信息记录实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeRecordEntity {
    private Long shareId;
    /**
     * 行为类型：UserShareBehaviorISAddType，true 增加 false 减少
     */
    private Boolean behaviorType;
}
