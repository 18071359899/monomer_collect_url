package com.collect.backend.controller.upload_file;

import com.collect.backend.common.enums.FileTypeEnum;
import com.collect.backend.utils.extractHttpInfo.HttpRequestParamsUtils;
import com.collect.backend.utils.fileType.MimeTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import static com.collect.backend.common.enums.FileTypeEnum.*;

/**
 * 预览文件接口
 */
@RestController
@RequestMapping("/uploadFile")
@Slf4j
public class PreviewFileController {
    @Value("${myfile.path}")
    private String prefix;
    public ResponseEntity<byte[]> getFileReader(String filePath,String contentType,String suffixName){
        try {
            File file = new File(prefix + filePath);
            if (file.exists()) {
                // 读取M3U8文件内容
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];

                fileInputStream.read(data);
                //文档类型需要去掉开头和结尾的空格，其他类型不管，否则会出问题
                if(Objects.nonNull(suffixName) && ( suffixName.equals("txt") || suffixName.equals("md"))){
                    String handleData = new String(data).trim();
                    data = handleData.getBytes();
                }
                fileInputStream.close();
                return ResponseEntity.ok()
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
//                        .contentLength(file.length())
                        .contentType(MediaType.valueOf(contentType))
                        .body(data);
            }else {
                return ResponseEntity.notFound().build();
            }
        }catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/preview/**")
    public ResponseEntity<byte[]> getM3U8Content(HttpServletRequest request) {
        String filePath = HttpRequestParamsUtils.getPathAllParams(request);
        String suffixName = MimeTypeUtils.getSuffixNoPoint(filePath);
        String contentType = "application/octet-stream";
        FileTypeEnum fileTypeByFileSuffix = MimeTypeUtils.getFileTypeByFileSuffix(suffixName);
        if(fileTypeByFileSuffix.getType().equals(MOVIE_TYPE.getType())){  //视频分块格式
            return getFileReader(filePath,contentType,null);
        }
        if(fileTypeByFileSuffix.getType().equals(OTHER_TYPE.getType()) ||
                fileTypeByFileSuffix.getType().equals(AUDIO_TYPE.getType())){
            return ResponseEntity.notFound().build();
        }

        if(fileTypeByFileSuffix.getType().equals(IMG_TYPE.getType())){
            contentType = "image/jpeg";
        }
        return getFileReader(filePath,contentType,suffixName);
    }
}
