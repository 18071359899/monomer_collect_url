package com.collect.backend.domain.vo.resp.common;

import lombok.Data;

import java.util.List;

/**
 * 通过页面底部进行分页处理
 */
@Data
public class CommonBottomPageVo<T> {
    private boolean isHave;  //是否还有数据，根据这个属性来进行分页处理
    private List<T> data;    //数据
}
