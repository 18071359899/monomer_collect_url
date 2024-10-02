package com.collect.backend.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 关注页面内容数据
 */
@Data
@AllArgsConstructor
public class FollowShareListVo {
    private List<ShareVo> data;    //数据
//    private boolean isHave;  //是否还有数据，根据这个属性来进行分页处理
    private Long lastTime;  // 返回最后一次查询到的时间，根据这个时间去做分页，下次传过来
    private Integer offset;  // 返回跳过相同时间的数据，下次传过来
}
