package com.collect.backend.common.exception;

import cn.hutool.http.ContentType;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.utils.json.JsonUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.Charsets;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
public enum HttpErrorEnum {
    ACCESS_DENIDED(401,"登陆失败请重新登陆");

    private Integer httpCode;
    private String desc;
    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(httpCode);
        response.setContentType(ContentType.JSON.toString(Charsets.UTF_8));
        response.getWriter().write(JsonUtils.toStr(ResultUtils.error(httpCode,desc)));
    }
}
