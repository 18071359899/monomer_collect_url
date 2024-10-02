package com.collect.backend.domain.vo.req;

import com.collect.backend.domain.vo.req.common.CommonPage;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CommentListReq extends CommonPage {
    @NotNull(message = "分享编号不能为空")
    private Long shareId;
}
