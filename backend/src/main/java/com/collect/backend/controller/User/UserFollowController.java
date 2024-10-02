package com.collect.backend.controller.User;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.UserFollow;
import com.collect.backend.domain.vo.req.common.HomePageReq;
import com.collect.backend.domain.vo.resp.UserFollowVo;
import com.collect.backend.service.user.UserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/follow")
public class UserFollowController {
    @Autowired
    private UserFollowService userFollowService;

    /**
     * 当前用户关注别人
     * @param
     * @return
     */
    @PostMapping("/add/")
    public BaseResponse<String> add(@RequestBody UserFollow userFollow){
        return userFollowService.add(userFollow);
    }

    /**
     * 当前用户取消关注别人
     * @param
     * @return
     */
    @PostMapping("/cancel/")
    public BaseResponse<String> delete(@RequestBody UserFollow userFollow){
        return userFollowService.delete(userFollow);
    }
    /**
     * 获取某个用户关注的列表
     */
    @GetMapping("/list/")
    public BaseResponse<Page<UserFollowVo>> list(HomePageReq homePageReq){
        return userFollowService.list(homePageReq,"user_id");
    }
    /**
     * 获取某个用户粉丝列表
     */
    @GetMapping("/fans/list/")
    public BaseResponse<Page<UserFollowVo>> fansList(HomePageReq homePageReq){
        return userFollowService.list(homePageReq,"follow_user_id");
    }
}
