package com.collect.backend.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.collect.backend.domain.entity.ReadCount;

import com.collect.backend.mapper.ReadCountMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ReadCountDao  extends ServiceImpl<ReadCountMapper, ReadCount> {
    /**
     * 更新阅读计数，不存在则新增，存在则更新
     */
    public Integer updateReadCount(Long documentId){
        ReadCount readCount = lambdaQuery().eq(ReadCount::getDocumentId, documentId).one();
        Integer cnt = 1;
        if(Objects.isNull(readCount)){
            Date date = new Date();
            getBaseMapper().insert(new ReadCount(null,documentId,cnt,date,date));
        }else{
            cnt = readCount.getCnt() + 1;
            lambdaUpdate().eq(ReadCount::getDocumentId, documentId)
                    .set(ReadCount::getUpdateTime,new Date())
                    .setSql("`cnt`=`cnt`+1").update();
        }
        return cnt;
    }

    /**
     * 查询阅读计数表
     */
    public ReadCount queryReadCount(Long documentId){
        return lambdaQuery().eq(ReadCount::getDocumentId, documentId).one();
    }
    /**
     * 获取阅读量
     */
    public Integer getReadCnt(Long documentId){
        AtomicReference<Integer> readCnt = new AtomicReference<>(1);
        ReadCount readCount = queryReadCount(documentId);
        Optional.ofNullable(readCount).ifPresent(newReadCount->{
            readCnt.set(newReadCount.getCnt());
        });
        return readCnt.get();
    }

}
