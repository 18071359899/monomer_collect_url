package com.collect.backend.controller.upload_file;

import com.collect.backend.common.BaseResponse;
import com.collect.backend.config.minio.MinioProperties;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.domain.vo.req.DeleteListByIds;
import com.collect.backend.domain.vo.req.UnionFileReq;
import com.collect.backend.domain.vo.req.upload_file.AddMinioUploadFileReq;
import com.collect.backend.domain.vo.req.upload_file.AddUploadFileReq;
import com.collect.backend.domain.vo.resp.UploadFileInfoResp;
import com.collect.backend.domain.vo.resp.upload_file.UnionFileResp;
import com.collect.backend.service.impl.upload_file.MinioUploadFileServiceImpl;
import com.collect.backend.service.impl.upload_file.UploadFileServiceImpl;
import com.collect.backend.utils.assertBussion.AssertUtil;
import com.collect.backend.utils.extractHttpInfo.HttpRequestParamsUtils;
import com.collect.backend.utils.fileType.MimeTypeUtils;
import com.collect.backend.utils.minio.MinioUtils;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.collect.backend.common.Constants.IMAGES_URL_PREFIX;

@RestController
@RequestMapping("/minio/file")
@Slf4j
public class MinioUploadFileController {
    @Autowired
    private MinioUploadFileServiceImpl minioUploadFileService;
    @Autowired
    private UploadFileServiceImpl uploadFileService;

    public static void main(String[] args) {
        String test = "qwe";
        test.concat("qwe");
        System.out.println(test);
    }
    @Value("${myfile.path}")
    private String filePath;
    @SneakyThrows
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile){
        return minioUploadFileService.uploadFile(multipartFile);
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
       return minioUploadFileService.commonUploadFile(multipartFile,fileMd5,chunkIndex,chunks);
    }

    /**
     * 合并分片文件接口
     */
    @PostMapping("/union/uploadFile")
    public BaseResponse<String> unionFile(@Valid UnionFileReq unionFileReq){ //合并文件
        return minioUploadFileService.unionFile(unionFileReq);
    }
    /**
     * 新增上传文件接口
     */
    @PostMapping("/add/uploadFile")
    public BaseResponse<Long> addUploadFile(@RequestBody @Valid AddMinioUploadFileReq addUploadFileReq){ //todo 改变参数
        //todo  改造流媒体分片并使用minio存储
        return minioUploadFileService.addUploadFile(addUploadFileReq);
    }

    /**
     *  删除上传文件接口
     * @param deleteListByIds
     * @return
     */
    @PostMapping("/delete/uploadFile")  //如果存的文件数大于1份，就先不删除文件
    public BaseResponse<String> deleteUploadFile(@RequestBody @Valid DeleteListByIds deleteListByIds){
        return minioUploadFileService.deleteUploadFile(deleteListByIds.getIds());
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
        minioUploadFileService.downloadFile(path,response,code);
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
