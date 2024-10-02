package com.collect.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息通知类型的枚举
 */
@Getter
@AllArgsConstructor
public enum MessageNotifyTypeEnum {
    USER_FOLLOW(0, "用户关注了某人"),
    USER_AGREE_ARTICLE(1, "用户点赞了某篇文章"),
    USER_AGREE_COMMENT(2, "用户点赞了某个评论"),
    USER_REPLY_COMMENT(3, "用户回复了某个评论"),
    USER_COLLECT_ARTICLE(4, "用户收藏了某篇文章"),
    USER_COMMENT_ARTICLE(5, "用户评论了某篇文章");
    public static MessageNotifyTypeEnum getByType(int type){
        for (MessageNotifyTypeEnum constants : values()) {
            if (constants.getType() == type) {
                return constants;
            }
        }
        return null;
    }
    /**
     * 消息通知类型
     */
    private final int type;
    /**
     * 描述
     */
    private final String desc;
}
