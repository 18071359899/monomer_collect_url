package com.collect.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对用户行为的枚举
 */
@Getter
@AllArgsConstructor
public enum UserShareBehaviorType{
    SHARE_AGREE(0, "用户点赞帖子"),
    SHARE_COLLECT(1, "用户收藏帖子"),
    COMMENT_AGREE(2, "用户点赞评论");
    public static UserShareBehaviorType getByType(int type){
        for (UserShareBehaviorType constants : values()) {
            if (constants.getType() == type) {
                return constants;
            }
        }
        return null;
    }
    /**
     * 用户行为类型
     */
    private final int type;

    /**
     * 描述
     */
    private final String desc;
}
