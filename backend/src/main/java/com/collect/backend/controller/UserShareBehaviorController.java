package com.collect.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.vo.req.ShareUserBehaviorReq;
import com.collect.backend.domain.vo.req.common.HomePageReq;
import com.collect.backend.domain.vo.resp.ShareVo;
import com.collect.backend.service.UserShareBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/share/behavior")
public class UserShareBehaviorController {
    @Autowired
    private UserShareBehaviorService userShareBehaviorService;
    /**
     *  用户点赞、取消点赞了某个评论
     */
    @PostMapping("/comment/")
    public BaseResponse<Integer> shareBehaviorComment(@Valid @RequestBody ShareUserBehaviorReq shareUserBehaviorReq){
        return userShareBehaviorService.shareBehaviorComment(shareUserBehaviorReq);
    }
    /**
     * 查询某个用户的收藏列表
     * @param
     * @return
     */
    @GetMapping("/collect/list/")
    public BaseResponse<Page<ShareVo>> listCollect(HomePageReq homePageReq){
        return userShareBehaviorService.listCollect(homePageReq);
    }

}
