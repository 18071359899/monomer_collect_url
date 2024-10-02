package com.collect.backend.domain.vo.req;


import com.collect.backend.domain.vo.resp.directory.CommonDirectory;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 粘贴到执行目录的参数
 */
@Data
public class PasteToDiectoryReq {
    @NotNull(message = "父级目录不能为空")
    private Long pid;
    @NotNull(message = "剪贴的内容不能为空")
    private List<CommonDirectory> directoryVos;
}
