package com.collect.backend.domain.vo.resp;

import com.collect.backend.domain.entity.Share;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShareVo extends Share {
    private String username;
    private String photo;
    /**
     * 当前用户是否点赞,1 点赞， 0 未点赞
     */
    private Integer isLike;
    /**
     * 当前用户是否收藏,1 点赞， 0 未点赞
     */
    private Integer isCollect;
}
