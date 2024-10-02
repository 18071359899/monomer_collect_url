package com.collect.backend.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.UserFollow;
import com.collect.backend.domain.vo.req.common.HomePageReq;
import com.collect.backend.domain.vo.resp.UserFollowVo;

/**
* @author Administrator
* @description 针对表【user_follow(用户关注的信息表)】的数据库操作Service
* @createDate 2024-04-10 23:37:46
*/
public interface UserFollowService{

    BaseResponse<String> add(UserFollow userFollow);

    BaseResponse<String> delete(UserFollow userFollow);

    BaseResponse<Page<UserFollowVo>> list(HomePageReq homePageReq, String attributes);
}
