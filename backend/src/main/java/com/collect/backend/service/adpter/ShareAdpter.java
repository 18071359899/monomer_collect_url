package com.collect.backend.service.adpter;


import cn.hutool.core.bean.BeanUtil;
import com.collect.backend.dao.*;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.entity.User;
import com.collect.backend.domain.vo.resp.ShareVo;
import com.collect.backend.utils.ManageUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShareAdpter {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserShareBehaviorDao shareBehaviorDao;
    @Autowired
    private ReadCountDao readCountDao;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private UserAgreeShareDao userAgreeShareDao;
    @Autowired
    private UserShareBehaviorDao userShareBehaviorDao;

    public ReadCountDao getReadCountDao() {
        return readCountDao;
    }

    public ShareVo getShareVo(Share share, Long userId) {
        ShareVo shareVo = new ShareVo();
        BeanUtil.copyProperties(share,shareVo);
        //查询用户信息
        User user = userDao.getBaseMapper().selectById(shareVo.getUserId());
        Long shareId = share.getId();

        shareVo.setPhoto(user.getPhoto());
        shareVo.setUsername(user.getUsername());
        //查询当前用户收否点赞收藏
        shareVo.setIsLike(shareBehaviorDao.queryIsLikeShare(userId,shareVo.getId()));
        shareVo.setIsCollect(shareBehaviorDao.queryIsCollectShare(userId,shareVo.getId()));
        //查询该文章对应的总数
        shareVo.setReadingCnt(readCountDao.getReadCnt(shareId));
        shareVo.setCommentCnt(commentDao.queryCommentCntShare(shareId));
        shareVo.setLikeCnt(userAgreeShareDao.getReadCnt(shareId));
        shareVo.setCollectCnt(userShareBehaviorDao.queryCollectCntShare(shareId));
        return shareVo;
    }

    public List<ShareVo> getShareVoList(List<Share> records){
        List<ShareVo> shareVoList = new ArrayList<>();
        Long userId = ManageUserInfo.getUser().getId();
        records.forEach(value->{
            shareVoList.add(getShareVo(value,userId));
        });
        return shareVoList;
    }
}
