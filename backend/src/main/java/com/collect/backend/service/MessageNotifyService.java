package com.collect.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.MessageNotify;
import com.baomidou.mybatisplus.extension.service.IService;
import com.collect.backend.domain.vo.req.AllReadMessageNotifyReq;
import com.collect.backend.domain.vo.req.MessageNotifyListReq;
import com.collect.backend.domain.vo.req.common.DeleteDataByIds;
import com.collect.backend.domain.vo.resp.MessageNotifyVo;

/**
* @author Administrator
* @description 针对表【message_notify(用户消息通知表)】的数据库操作Service
* @createDate 2024-07-22 19:06:53
*/
public interface MessageNotifyService{
    BaseResponse<Page<MessageNotifyVo>> messageNotifyList(MessageNotifyListReq messageNotifyListReq);

    BaseResponse<String> deleteMessageNotify(DeleteDataByIds deleteDataByIds);

    BaseResponse<String> allReadMessageNotify(AllReadMessageNotifyReq allReadMessageNotifyReq);

    BaseResponse<String> readMessageNotify(Long messageNotifyId);

    BaseResponse<Integer> messageNotifyTotal();
}
