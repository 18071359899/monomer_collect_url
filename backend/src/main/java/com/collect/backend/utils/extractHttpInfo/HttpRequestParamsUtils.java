package com.collect.backend.utils.extractHttpInfo;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取http请求地址中的信息
 */
public class HttpRequestParamsUtils {
    public static AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 通过请求头拿到路径参数中**匹配的所有参数信息
     * @param request
     * @return
     */
    public static String getPathAllParams(HttpServletRequest request){
        // 获取完整的路径
        String uri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        // 获取映射的路径
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        // 截取带“**”的参数
        String pathAllParams = antPathMatcher.extractPathWithinPattern(pattern,uri);
        return pathAllParams;
    }
}
