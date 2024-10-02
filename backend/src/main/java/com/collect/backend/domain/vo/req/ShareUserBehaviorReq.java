package com.collect.backend.domain.vo.req;


import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *  用户对帖子所做出行为的请求参数
 */
@Data
public class ShareUserBehaviorReq {
    @NotNull(message = "业务内容不能为空")
    private Long id;
    /**
     * 行为类型：0，用户点赞   1，用户收藏
     */
    @NotNull(message = "类型不能为空")
    private Integer type;
    /**
     * 增加或减少 ： 0 减少  1 增加
     */
    @NotNull(message = "业务类型不能为空")
    private Integer isAdd;
}
