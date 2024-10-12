package com.collect.backend.service.impl.upload_file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.Constants;
import com.collect.backend.common.ErrorCode;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.common.enums.LogicDeleteTypeEnum;
import com.collect.backend.common.enums.UploadStatusEnum;
import com.collect.backend.common.exception.BusinessException;
import com.collect.backend.common.exception.CommonErrorEnum;
import com.collect.backend.dao.UploadFileDao;
import com.collect.backend.dao.UserDao;
import com.collect.backend.dao.UserUseTotalDao;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.domain.entity.User;
import com.collect.backend.domain.entity.UserUseTotal;
import com.collect.backend.domain.vo.req.UnionFileReq;
import com.collect.backend.domain.vo.req.upload_file.AddUploadFileReq;
import com.collect.backend.domain.vo.resp.UploadFileInfoResp;
import com.collect.backend.domain.vo.resp.upload_file.UnionFileResp;
import com.collect.backend.service.directory.DirectoryAdapter;
import com.collect.backend.service.impl.user_use_file_space.UserUseTotalAdapter;
import com.collect.backend.utils.*;
import com.collect.backend.utils.assertBussion.AssertUtil;
import com.collect.backend.utils.file.FileUtils;
import com.collect.backend.utils.fileType.MimeTypeUtils;
import com.collect.backend.utils.redis.RedisUtils;
import com.collect.backend.utils.uuid.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.collect.backend.common.Constants.getDownloadKey;


@Service
@Slf4j
public class UploadFileServiceImpl {
    public static final int TIME = 10;
    public static final int DOWNLOAD_CODE_TIME = 2;
    @Autowired
    private UploadFileDao uploadFileDao;

    public UploadFileDao getUploadFileDao() {
        return uploadFileDao;
    }


    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserUseTotalAdapter userUseTotalDao;
    public static final int HEIGHT = 40;
    public static final int WIDTH = 40;
    @Value("${myfile.path}")
    private String filePath;
    private final String tempFolder = "tmp/";

    public BaseResponse<UploadFileInfoResp> queryUploadFile(String fileMd5,Long pid,Long fileSize,String fileName){
        UploadFileInfoResp uploadFileInfoResp = new UploadFileInfoResp();
        UploadFile queryByMd5 = uploadFileDao.queryByMd5(fileMd5);
        //秒传
        if(Objects.nonNull(queryByMd5)){
            uploadFileInfoResp.setFileStatus(UploadStatusEnum.UPLOAD_SECONDS.getStatus());
            queryByMd5.setId(null);queryByMd5.setPid(pid);queryByMd5.setFileName(fileName);
            uploadFileDao.getBaseMapper().insert(queryByMd5);
            String isNoSpace = getIsNoSpace(fileSize);
            if(Objects.nonNull(isNoSpace)){
                return ResultUtils.error(CommonErrorEnum.BUSINESS_ERROR.getErrorCode(),isNoSpace);
            }
            //更新用户使用文件空间
            userUseTotalDao.updateUserUploadFileSpace(fileSize,0);
            return ResultUtils.success(uploadFileInfoResp);
        }
        return ResultUtils.success(uploadFileInfoResp);
    }
    public boolean isExceedUseSpace(Long use,Long total){
        return use > total ? true : false;
    }
    public BaseResponse<UploadFileInfoResp> commonUploadFile(MultipartFile multipartFile,
                                                             String fileMd5,  //使用md5值来命名临时目录
                                                             Integer chunkIndex, //当前是第几个分片
                                                             Integer chunks  //总共有多少个分片
    ){
        if(chunkIndex >= chunks){
            return ResultUtils.error(CommonErrorEnum.BUSINESS_ERROR.getErrorCode(),"当前分片大于等于总分片");
        }
        UploadFileInfoResp uploadFileInfoResp = new UploadFileInfoResp();
        File tempFileFolder = null;
        boolean fileIsSuccess = true;  // 文件是否上传成功标志，默认成功
        try{
            //暂存临时目录
            String tempFolderName = filePath + tempFolder;
            tempFileFolder = new File(tempFolderName+fileMd5);
            if(!tempFileFolder.exists()){  //创建该目录
                tempFileFolder.mkdirs();
            }
            File newFile = new File(tempFileFolder.getPath() + "/" + chunkIndex);
            if(newFile.exists() && newFile.length() == multipartFile.getSize()){  //断点续传，不需要在重新上传
                uploadFileInfoResp.setFileStatus(UploadStatusEnum.UPLOADING.getStatus());
                return ResultUtils.success(uploadFileInfoResp);
            }
            multipartFile.transferTo(newFile);
            uploadFileInfoResp.setFileStatus(UploadStatusEnum.UPLOADING.getStatus());
            return ResultUtils.success(uploadFileInfoResp);
        }catch (Throwable e){
            log.error("文件上传失败 ",e);
            fileIsSuccess = false;
        }finally {
            if(!fileIsSuccess && Objects.nonNull(tempFileFolder)){  // 失败，删除临时目录
                FileUtil.del(tempFileFolder);
            }
        }
        return ResultUtils.success(uploadFileInfoResp);
    }
    public String getRealFileName(String monthDay,String realFileName){
        return monthDay +  "/" + realFileName;
    }
    public String getTargetFolderName(String monthDay){  //拼接目标目录
        return filePath + monthDay;
    }
    public String getTargetFileName(String newFilePath){  //拼接目标文件
        return filePath + newFilePath;
    }
    public String getTempFolderName(String fileMd5){  //拼接临时md5值目录
        return filePath + tempFolder + fileMd5;
    }
    public BaseResponse<UnionFileResp> unionFile(UnionFileReq unionFileReq){ //合并文件
        String tempFolderName = null;
        try {
            String fileName = unionFileReq.getFileName();
            String fileMd5 = unionFileReq.getFileMd5();
            //文件夹路径
            String monthDay = DateUtil.format(new Date(),"yyyy-MM-dd");
            //真实的文件名
            String realFileName = IdUtils.simpleUUID() + MimeTypeUtils.getSuffix(fileName);
            //路径
            String newFilePath = getRealFileName(monthDay,realFileName);
            //临时目录
            tempFolderName = getTempFolderName(fileMd5);
            //目标目录
            String targetFolderName = getTargetFolderName(monthDay);
            File targetFolder = new File(targetFolderName);
            if(!targetFolder.exists()){
                targetFolder.mkdirs();
            }
            //目标文件
            String targetFileName = filePath + newFilePath;
            union(tempFolderName,targetFileName,realFileName,false);
            UnionFileResp unionFileResp = new UnionFileResp();
            unionFileResp.setMonthDay(monthDay);
            unionFileResp.setRealFileName(realFileName);
            return ResultUtils.success(unionFileResp);
        }catch (Throwable e){
            if(Objects.nonNull(tempFolderName)){  // 删除临时目录
                FileUtil.del(tempFolderName);
            }
            return ResultUtils.error(ErrorCode.OPERATION_ERROR);
        }
    }

    public BaseResponse<Long> addUploadFile(AddUploadFileReq addUploadFileReq){
        Date date = new Date();
        String fileMd5 = addUploadFileReq.getMd5();
        String fileName = addUploadFileReq.getFileName();
        String realFileName = addUploadFileReq.getRealFileName();
        String monthDay = addUploadFileReq.getMonthDay();
        String newFilePath = getRealFileName(monthDay,realFileName);
        String targetFileName = getTargetFileName(newFilePath);
        Long fileSize = addUploadFileReq.getFileSize();
        String suffixNoPoint = MimeTypeUtils.getSuffixNoPoint(fileName);
        //合并成功，记录数据库
        UploadFile newUploadFileData = new UploadFile(
                null,fileMd5,date,newFilePath,fileName, MimeTypeUtils.getFileTypeByFileSuffix(suffixNoPoint).getType(),
                addUploadFileReq.getPid(),null, ManageUserInfo.getUser().getId(),date,0 //通过ffmepeg生成缩略图
        );
        int insert = uploadFileDao.getBaseMapper().insert(newUploadFileData);
        AssertUtil.isTrue(insert > 0,"新增失败");


        //更新用户使用文件空间
        userUseTotalDao.updateUserUploadFileSpace(fileSize,0);
        Long id = newUploadFileData.getId();
        Map<String,Object> map = new HashMap<>();
        map.put("monthDay",monthDay);
        map.put("realFileName",realFileName);
        map.put("targetFileName",targetFileName);
        map.put("fileName",fileName);
        map.put("id",id.toString());

        rabbitTemplate.convertAndSend("work.queue",map);
        return ResultUtils.success(id);
    }

    public void union(String dirPath, String toFilePath, String fileName, Boolean del){
        File dir = new File(dirPath);
        if(!dir.exists()){
            throw new BusinessException("目录不存在");
        }
        File[] fileList = dir.listFiles(); //获取该目录的所有文件
        File targetFile = new File(toFilePath);
        RandomAccessFile writeFile = null;
        try{
            writeFile = new RandomAccessFile(targetFile,"rw");
            byte [] b = new byte[10 * 1024] ;//一次读取1MB
            for (int i = 0; i < fileList.length; i++) {
                File chunkFile = new File(dirPath + "/" +i);
                RandomAccessFile readFile = null;
                try {
                    readFile = new RandomAccessFile(chunkFile,"r");
                    while ( readFile.read(b) != -1 ){
                        writeFile.write(b,0, b.length);
                    }
                }catch (Exception e){
                    log.error("合并分片失败",e);
                    throw  new BusinessException("合并分片失败");
                }finally {
                    if(Objects.nonNull(readFile))
                        readFile.close();
                }
            }
        } catch (Exception e) {
            log.error("合并文件：{}失败",fileName,e);
            throw new RuntimeException(e);
        }finally {
            try {
                if(Objects.nonNull(writeFile))
                    writeFile.close();
                if(del && dir.exists()){  // 删除临时目录
                    FileUtil.del(dir);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public BaseResponse<String> deleteUploadFile(List<Long> ids) {
        Long reduceUserSpace = 0L;
        for (Long id : ids) {
            UploadFile uploadFile = uploadFileDao.getBaseMapper().selectById(id);
            if(Objects.isNull(uploadFile)) continue;
            File file = new File(filePath + uploadFile.getPath());
            if(file.exists()){
                reduceUserSpace += file.length();
            }
            int isSuccess = uploadFileDao.getBaseMapper().deleteById(id);
            if(isSuccess > 0){
                UploadFile md5UploadFile = uploadFileDao.queryByMd5(uploadFile.getMd5());
                if(Objects.isNull(md5UploadFile)){ //如果该文件被多个用户同时使用，也就是秒传的逻辑，就先不删除该文件，否则删除
                    FileUtil.del(file);
                }
            }
        }


        // 更新用户使用空间
        userUseTotalDao.updateUserUploadFileSpace(reduceUserSpace,1);
        return ResultUtils.success("ok");
    }

    public String logicDeleteUploadFile(List<Long> ids){
        for(Long id: ids){
            uploadFileDao.logicDelete(id);
        }
        return "ok";
    }
    public String getIsNoSpace(Long fileSize){
        UserUseTotal userUseTotalByUserId = userUseTotalDao.getUserUseTotalDao().
                getUserUseTotalByUserId(ManageUserInfo.getUser().getId());
        userUseTotalByUserId.setUserUse(userUseTotalByUserId.getUserUse() + fileSize);
        if(isExceedUseSpace(userUseTotalByUserId.getUserUse(), userUseTotalByUserId.getUserTotal())){
            return "空间不够了，上传失败";
        }
        return null;
    }
    public BaseResponse<String> selectCurrUserIsSpace(Long fileSize) {
        String isNoSpace = getIsNoSpace(fileSize);
        if(Objects.nonNull(isNoSpace)){
            return ResultUtils.error(CommonErrorEnum.BUSINESS_ERROR.getErrorCode(),isNoSpace);
        }
        return ResultUtils.success("ok");
    }

    public BaseResponse<String> resetFileName(UploadFile uploadFile) {
        Long id = uploadFile.getId();
        Long pid = uploadFile.getPid();
        String fileName = uploadFile.getFileName();
        List<UploadFile> uploadFileList = uploadFileDao.getFileByPidUserId(ManageUserInfo.getUser().getId(),pid).stream().
                filter((uploadFile1 -> !uploadFile1.getId().equals(id))).collect(Collectors.toList());
        String searchFileNameIsRepeat = DirectoryAdapter.searchFileNameIsRepeat(fileName, uploadFileList);
        if(StringUtils.isNotBlank(searchFileNameIsRepeat)){
            uploadFile.setFileName(searchFileNameIsRepeat);
        }
        int update = uploadFileDao.getBaseMapper().updateById(uploadFile);
        AssertUtil.isTrue(update > 0,"更新失败");
        return ResultUtils.success(uploadFile.getFileName());
    }

    public void downloadFile(String path, HttpServletResponse response,String code) {
        String downloadKey = getDownloadKey(code);
        Boolean isExist = RedisUtils.get(downloadKey, Boolean.class);
        AssertUtil.isNotEmpty(isExist,"你无权下载该文件");
        RedisUtils.del(downloadKey);
        try
        {
            String downloadPath = filePath + path;
            // 下载名称
            String downloadName = MimeTypeUtils.getFileNameByPath(path);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    public void cutUploadFile(List<UploadFile> uploadFileList,Date date,Long pid) {
            uploadFileList.forEach(value->{
                value.setPid(pid);
                value.setUpdateTime(date);
                uploadFileDao.getBaseMapper().updateById(value);
            });
    }

    public void copyUploadList(List<UploadFile> uploadFileList, Long userId, Long pid, Date date) {
        uploadFileList.forEach(value->{
            UploadFile uploadFile = uploadFileDao.getBaseMapper().selectById(value.getId());
            uploadFile.setPid(pid);uploadFile.setId(null);uploadFile.setUserId(userId);
            uploadFile.setCreateTime(date); uploadFile.setUpdateTime(date);
            uploadFile.setIsDelete(LogicDeleteTypeEnum.NO_DELETE.getType());
            uploadFileDao.getBaseMapper().insert(uploadFile);
        });
    }
    public BaseResponse<String> getDownloadCode() {
        String randomString = RandomUtil.randomString(10);
        RedisUtils.set(getDownloadKey(randomString),true, DOWNLOAD_CODE_TIME, TimeUnit.SECONDS);
        return ResultUtils.success(randomString);
    }
}
