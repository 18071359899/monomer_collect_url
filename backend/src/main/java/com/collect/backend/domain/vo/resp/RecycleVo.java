package com.collect.backend.domain.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class RecycleVo {
    @NotNull(message = "id不能为空")
    private Long id;
    /**
     * 分类名称
     */
    private String name;

    /**
     * 网站标题
     */
    private String title;

    /**
     * 网站图标
     */
    private String icon;
    /**
     * 目录分类： 0，文件夹  1，网址
     */
    @NotNull(message = "分类不能为空")
    private Integer type;
    /**
     * 删除Id
     */
    @NotNull(message = "关联id不能为空")
    private Long deleteId;

    /**
     * 删除时间
     */
    @JsonFormat(timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm")
    private Date deleteTime;
}
