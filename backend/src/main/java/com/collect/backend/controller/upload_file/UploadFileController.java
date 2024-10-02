package com.collect.backend.controller.upload_file;

import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.domain.vo.req.DeleteListByIds;
import com.collect.backend.domain.vo.req.UnionFileReq;
import com.collect.backend.domain.vo.req.upload_file.AddUploadFileReq;
import com.collect.backend.domain.vo.resp.UploadFileInfoResp;
import com.collect.backend.domain.vo.resp.upload_file.UnionFileResp;
import com.collect.backend.service.impl.upload_file.UploadFileServiceImpl;
import com.collect.backend.utils.assertBussion.AssertUtil;
import com.collect.backend.utils.extractHttpInfo.HttpRequestParamsUtils;
import com.collect.backend.utils.fileType.MimeTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.collect.backend.common.Constants.IMAGES_URL_PREFIX;

@RestController
@RequestMapping("/file")
@Slf4j
public class UploadFileController {
    @Autowired
    private UploadFileServiceImpl uploadFileService;
    @Value("${myfile.path}")
    private String filePath;
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile){
        String originalFilename = multipartFile.getOriginalFilename();
        String name = UUID.randomUUID().toString() ;
        String path = name + MimeTypeUtils.getSuffix(originalFilename);
        File file = new File(filePath + path);
        try {
            String url = IMAGES_URL_PREFIX + path;
            //文件保存到服务器上
            multipartFile.transferTo(file);
            return url;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 查询大文件的md5值，秒传逻辑
     */
    @PostMapping("/query/uploadFile")
    public BaseResponse<UploadFileInfoResp> queryUploadFile(String fileMd5,Long pid,Long fileSize,String fileName){
        return uploadFileService.queryUploadFile(fileMd5,pid,fileSize,fileName);
    }
    /**
     * 分片上传接口
     */
    @PostMapping("/common/uploadFile")
    public BaseResponse<UploadFileInfoResp> commonUploadFile(MultipartFile multipartFile,
                                                             String fileMd5,  //使用md5值来命名临时目录
                                                             Integer chunkIndex, //当前是第几个分片
                                                             Integer chunks  //总共有多少个分片
    ){
       return uploadFileService.commonUploadFile(multipartFile,fileMd5,chunkIndex,chunks);
    }

    /**
     * 合并分片文件接口
     */
    @PostMapping("/union/uploadFile")
    public BaseResponse<UnionFileResp> unionFile(@Valid UnionFileReq unionFileReq){ //合并文件
        return uploadFileService.unionFile(unionFileReq);
    }
    /**
     * 新增上传文件接口
     */
    @PostMapping("/add/uploadFile")
    public BaseResponse<Long> addUploadFile(@RequestBody @Valid AddUploadFileReq addUploadFileReq){
        return uploadFileService.addUploadFile(addUploadFileReq);
    }

    /**
     *  删除上传文件接口
     * @param deleteListByIds
     * @return
     */
    @PostMapping("/delete/uploadFile")  //如果存的文件数大于1份，就先不删除文件
    public BaseResponse<String> deleteUploadFile(@RequestBody @Valid DeleteListByIds deleteListByIds){
        return uploadFileService.deleteUploadFile(deleteListByIds.getIds());
    }

    /**
     *   重命名文件名称
     */
    @PostMapping("/update/fileName")
    public BaseResponse<String> resetFileName(@RequestBody @Valid UploadFile uploadFile){
        AssertUtil.isNotEmpty(uploadFile.getId(),"文件编号不能为空");
        return uploadFileService.resetFileName(uploadFile);
    }

    /**
     * 查看当前用户是否有上传该文件的空间
     */
    @GetMapping("/get/space/success")
    public BaseResponse<String> selectCurrUserIsSpace(Long fileSize){
        return uploadFileService.selectCurrUserIsSpace(fileSize);
    }
    /**
     * 获取下载链接code
     */
    @GetMapping("/get/download/code")
    public BaseResponse<String> getDownloadCode(){
        return uploadFileService.getDownloadCode();
    }
    /**
     * 下载
     */
    @GetMapping("/download/{code}/**")
    public void downloadFile(HttpServletRequest httpServletRequest, HttpServletResponse response
    ,@PathVariable("code") String code){
        String path = HttpRequestParamsUtils.getPathAllParams(httpServletRequest);
        uploadFileService.downloadFile(path,response,code);
    }
    //todo 是否需要增加一个接口将上传时暂停或删除对应临时目录删除的对应接口
    /**
     *
     */
}


//
//生成base64编码
//            byte[] b = new byte[0];
//        try {
//            b = multipartFile.getBytes();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        String str = Base64.getEncoder().encodeToString(b);
//        System.out.println("data:image/jpeg;base64," + str);
