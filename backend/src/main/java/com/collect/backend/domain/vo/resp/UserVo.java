package com.collect.backend.domain.vo.resp;


import lombok.Data;

@Data
public class UserVo {
    private Long id;
    /**
     *
     */
    private String username;
    /**
     *
     */
    private String photo;
    /**
     * 关注数
     */
    private Integer follow;
    /**
     * 粉丝数
     */
    private Integer fans;
    /**
     * 简介
     */
    private String introduction;
    /**
     * 当前登录用户是否关注了他
     */
    private Integer isFollow; //0：未关注   1；已关注
}
