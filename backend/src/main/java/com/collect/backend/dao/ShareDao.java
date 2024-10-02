package com.collect.backend.dao;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.vo.req.common.CursorPageBaseReq;
import com.collect.backend.domain.vo.resp.common.CursorPageBaseResp;
import com.collect.backend.mapper.ShareMapper;
import com.collect.backend.utils.cursor.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShareDao extends ServiceImpl<ShareMapper, Share> {
    /**
     * 根据id列表查询分享列表
     */
    public List<Share> selectShareListByIds(List<Long> ids){
        String idStr = StrUtil.join(",", ids);
        return query().in("id", ids)
                .last("ORDER BY FIELD(id," + idStr + ")").list();
    }

    public CursorPageBaseResp<Share> getSharePage(CursorPageBaseReq cursorPageBaseReq) {
        return CursorUtils.getCursorPageByMysql(this, cursorPageBaseReq,
                wrapper -> wrapper.orderByDesc(Share::getId), Share::getId);
    }

    public void updateShareLike(Long id) {
        lambdaUpdate().eq(Share::getId,id)
                .setSql("`like`=`like`+1")
                .update();
    }
}
