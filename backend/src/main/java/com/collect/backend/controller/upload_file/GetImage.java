package com.collect.backend.controller.upload_file;


import com.collect.backend.utils.extractHttpInfo.HttpRequestParamsUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping(value="/images")
public class GetImage {
    @Value("${myfile.path}")
    private String filePath;

    /**
     * 返回图片
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/get/**",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImage(HttpServletRequest httpServletRequest) throws Exception {
        String path = HttpRequestParamsUtils.getPathAllParams(httpServletRequest);
        File file = new File(filePath + path);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        return bytes;
    }
}
