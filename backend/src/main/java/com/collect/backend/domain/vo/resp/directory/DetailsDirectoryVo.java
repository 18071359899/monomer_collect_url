package com.collect.backend.domain.vo.resp.directory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DetailsDirectoryVo extends CommonDirectory{
    /**
     * 位置
     */
    private String position;
}
