package com.collect.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.vo.req.AllReadMessageNotifyReq;
import com.collect.backend.domain.vo.req.MessageNotifyListReq;
import com.collect.backend.domain.vo.resp.MessageNotifyVo;
import com.collect.backend.service.MessageNotifyService;
import com.collect.backend.utils.assertBussion.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *  当前用户消息通知controller
 */
@RestController
@RequestMapping("/messageNotify")
public class MessageNotifyController {
    @Autowired
    private MessageNotifyService messageNotifyService;

    /**
     * 通知列表
     * @param messageNotifyListReq
     * @return
     */
    @GetMapping("/list/")
    public BaseResponse<Page<MessageNotifyVo>> messageNotifyList(@Valid MessageNotifyListReq messageNotifyListReq){
        return messageNotifyService.messageNotifyList(messageNotifyListReq);
    }

    /**
     * 计算当前用户有多少消息通知
     * @param
     * @return
     */
    @GetMapping("/total/")
    public BaseResponse<Integer> messageNotifyTotal(){
        return messageNotifyService.messageNotifyTotal();
    }

    /**
     * 设置当前用户消息通知全部已读
     * @param allReadMessageNotifyReq
     * @return
     */
    @PostMapping("/allRead/")
    public BaseResponse<String> allReadMessageNotify(@Valid @RequestBody AllReadMessageNotifyReq allReadMessageNotifyReq){
        return messageNotifyService.allReadMessageNotify(allReadMessageNotifyReq);
    }
    @PostMapping("/read/")
    public BaseResponse<String> readMessageNotify(Long messageNotifyId){
        AssertUtil.isNotEmpty(messageNotifyId,"参数错误");
        return messageNotifyService.readMessageNotify(messageNotifyId);
    }
}
