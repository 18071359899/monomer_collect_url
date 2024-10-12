package com.collect.backend.domain.vo.resp;

import com.collect.backend.domain.entity.Share;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
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
    /**
     * 阅读量
     */
    private Integer readingCnt;
    /**
     * 点赞数
     */
    private Long likeCnt;
    /**
     * 评论数
     */
    private Integer commentCnt;
    /**
     * 收藏数
     */
    private Integer collectCnt;
}
