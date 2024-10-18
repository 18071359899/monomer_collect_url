package com.collect.backend.service.share;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.vo.req.ShareUserBehaviorReq;
import com.collect.backend.domain.vo.req.common.CursorPageBaseReq;
import com.collect.backend.domain.vo.req.common.HomePageReq;
import com.collect.backend.domain.vo.resp.FollowShareListVo;
import com.collect.backend.domain.vo.resp.ShareVo;
import com.collect.backend.domain.vo.resp.common.CursorPageBaseResp;

public interface ShareService {
    public BaseResponse<Long> addShare(Share share);

    BaseResponse<Boolean> updateShare(Share share);

    CursorPageBaseResp<ShareVo> listShare(CursorPageBaseReq cursorPageBaseReq);

    BaseResponse<Share> getShare(Long id);

    BaseResponse<String> userBehavior(ShareUserBehaviorReq share);

    BaseResponse<Page<ShareVo>> getInfoShare(HomePageReq homePageReq);

    BaseResponse<String> deleteShare(Long id);

    ShareVo getDetailShare(Long id);

    BaseResponse<FollowShareListVo> followListShare(Long max, Integer offset,Integer count);
}
