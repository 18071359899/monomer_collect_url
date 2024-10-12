package com.collect.backend.controller.share;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.vo.req.ShareUserBehaviorReq;
import com.collect.backend.domain.vo.req.common.CommonPage;
import com.collect.backend.domain.vo.req.common.CursorPageBaseReq;
import com.collect.backend.domain.vo.req.common.HomePageReq;
import com.collect.backend.domain.vo.resp.FollowShareListVo;
import com.collect.backend.domain.vo.resp.ShareVo;
import com.collect.backend.domain.vo.resp.common.CommonBottomPageVo;
import com.collect.backend.domain.vo.resp.common.CursorPageBaseResp;
import com.collect.backend.service.share.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/share")
public class ShareController {
    @Autowired
    private ShareService shareService;
    @PostMapping("/add/")
    public BaseResponse<Long> addShare(@Valid @RequestBody Share share){
        return shareService.addShare(share);
    }

    @PostMapping("/delete/")
    public BaseResponse<String> deleteShare(Long id){
        return shareService.deleteShare(id);
    }

    @PostMapping("/update/")
    public BaseResponse<Boolean> updateShare(@Valid @RequestBody Share share){
        return shareService.updateShare(share);
    }
    /**
     * 用户点赞、收藏、取消点赞、取消收藏了某个帖子
     */
    @PostMapping("/userBehavior/")
    public BaseResponse<String> updateUserBehavior(@Valid @RequestBody ShareUserBehaviorReq share){
        return shareService.userBehavior(share);
    }
    @GetMapping("/list/")
    public BaseResponse<CursorPageBaseResp<ShareVo>> listShare(@Valid CursorPageBaseReq cursorPageBaseReq){
        return ResultUtils.success(shareService.listShare(cursorPageBaseReq));
    }

    @GetMapping("/follow/list/")
    public BaseResponse<FollowShareListVo> followListShare(@RequestParam("lastId") Long max,
                                                           @RequestParam(value = "offset",defaultValue = "0") Integer offset,
                                                           @RequestParam(value = "count",defaultValue = "10") Integer count){
        return shareService.followListShare(max,offset,count);
    }

    /**
     * 查询某个用户的分享列表
     * @param
     * @return
     */
    @GetMapping("/get/info/")
    public BaseResponse<Page<ShareVo>> getInfoShare(HomePageReq homePageReq){
        return shareService.getInfoShare(homePageReq);
    }

    @GetMapping("/{id}/")
    public BaseResponse<Share> getShare(@PathVariable("id") Long id){
        return shareService.getShare(id);
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}/")
    public BaseResponse<ShareVo> getDetailShare(@PathVariable("id") Long id){
        return ResultUtils.success(shareService.getDetailShare(id));
    }


}
