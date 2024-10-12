package com.collect.backend.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.collect.backend.domain.entity.ReadCount;
import com.collect.backend.domain.entity.UserAgreeShare;

import com.collect.backend.mapper.UserAgreeShareMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class UserAgreeShareDao extends ServiceImpl<UserAgreeShareMapper, UserAgreeShare> {
    /**
     * 更新点赞计数，不存在则新增，存在则更新
     */
    public Long addAgreeCountOne(Long documentId){
        UserAgreeShare readCount = lambdaQuery().eq(UserAgreeShare::getShareId, documentId).one();
        Long cnt = 1L;
        if(Objects.isNull(readCount)){
            getBaseMapper().insert(new UserAgreeShare(null,documentId,cnt));
        }else{
            cnt = readCount.getCnt() + 1;
            addAgreePoints(documentId,1L);
        }
        return cnt;
    }

    /**
     * 按给定数量增加点赞
     * @return
     */
    public Boolean addAgreePoints(Long shareId,Long addCount){
        return lambdaUpdate().eq(UserAgreeShare::getShareId, shareId)
                .setSql("`cnt`=`cnt`+"+addCount.toString()).update();
    }

    /**
     * 查询点赞计数表
     */
    public UserAgreeShare queryReadCount(Long documentId){
        return lambdaQuery().eq(UserAgreeShare::getShareId, documentId).one();
    }
    /**
     * 获取点赞量
     */
    public Long getReadCnt(Long documentId){
        Long readCnt = 0L;
        UserAgreeShare readCount = queryReadCount(documentId);
        if(Objects.nonNull(readCount)){
            readCnt = readCount.getCnt();
        }else{
            getBaseMapper().insert(new UserAgreeShare(null,documentId,readCnt));
        }
        return readCnt;
    }
    /**
     * 增加文章的点赞数
     * @param shareId 文章id
     * @param addCount  添加的数量
     */
    public void updateAgreeCount(Long shareId,Long addCount){
        Boolean isSuccess = addAgreePoints(shareId, addCount);
        if(!isSuccess){  //更新失败是因为可能未有该数据，进行插入操作即可
            getBaseMapper().insert(new UserAgreeShare(null,shareId,addCount));
        }
    }
}
