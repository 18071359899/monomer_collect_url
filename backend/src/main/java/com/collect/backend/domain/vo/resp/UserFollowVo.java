package com.collect.backend.domain.vo.resp;

import com.collect.backend.domain.entity.UserFollow;
import lombok.Data;

@Data
public class UserFollowVo extends UserFollow {
    private String username;
    private String photo;
    /**
     * 简介
     */
    private String introduction;
    /**
     * 当前用户跟该用户的关系状态  0：未关注  1：已关注  2：互粉
     */
    private Integer linkStatus;

}
