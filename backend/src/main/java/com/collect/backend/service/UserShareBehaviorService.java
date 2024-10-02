package com.collect.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.vo.req.ShareUserBehaviorReq;
import com.collect.backend.domain.vo.req.common.HomePageReq;
import com.collect.backend.domain.vo.resp.ShareVo;
import com.collect.backend.domain.vo.resp.common.CommonBottomPageVo;

public interface UserShareBehaviorService {

    BaseResponse<Page<ShareVo>> listCollect(HomePageReq homePageReq);

    BaseResponse<Integer> shareBehaviorComment(ShareUserBehaviorReq shareUserBehaviorReq);
}
