package com.collect.backend.domain.vo.resp;

import com.collect.backend.domain.entity.MessageNotify;
import lombok.Data;

@Data
public class MessageNotifyVo extends MessageNotify {
    /**
     * 发送方用户名
     */
    private String sendUserUsername;
    /**
     * 发送方用户头像
     */
    private String sendPhoto;
    /**
     * 业务数据，比如帖子的标题
     */
    private String businessData;
    /**
     * 分享id,当用户的行为类型跟评论相关显示
     */
    private Long shareId;
}
