package com.collect.backend.common.event.listener;

import com.collect.backend.common.event.DeleteShareEvent;
import com.collect.backend.common.event.MessageNotifyEvent;
import com.collect.backend.dao.CommentDao;
import com.collect.backend.dao.MessageNotifyDao;
import com.collect.backend.dao.ShareDao;
import com.collect.backend.domain.entity.MessageNotify;
import com.collect.backend.domain.entity.Share;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 监听消息通知相关事件，进行插入操作
 */
@Component
public class DeleteShareListener {
    @Autowired
    private CommentDao commentDao;
    @EventListener(classes = DeleteShareEvent.class)
    @Async("threadPoolTaskExecutor")
    public void deleteCommentByShare(DeleteShareEvent deleteShareEvent){
        Share share = deleteShareEvent.getShare();
        commentDao.deleteCommentByShareId(share.getId());
    }

}
