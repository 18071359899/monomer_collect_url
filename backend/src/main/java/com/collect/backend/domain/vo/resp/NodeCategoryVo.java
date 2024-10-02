package com.collect.backend.domain.vo.resp;

import lombok.Data;

/**
 *   返回叶子节点的数据对应前端
 */
@Data
public class NodeCategoryVo {
    private Long id;
    private String label;
    private boolean leaf;  //跟element-plus属性对应,是否为叶子节点： 是：无下拉  否：有下拉
}
