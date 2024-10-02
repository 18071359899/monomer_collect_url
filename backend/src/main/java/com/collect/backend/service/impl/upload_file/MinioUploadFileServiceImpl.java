package com.collect.backend.service.impl.upload_file;

import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.common.enums.UploadStatusEnum;
import com.collect.backend.common.exception.CommonErrorEnum;
import com.collect.backend.config.minio.MinioProperties;
import com.collect.backend.dao.UploadFileDao;
import com.collect.backend.dao.UserUseTotalDao;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.domain.entity.UserUseTotal;
import com.collect.backend.domain.vo.cache.UserSpaceRedisVo;
import com.collect.backend.domain.vo.req.UnionFileReq;
import com.collect.backend.domain.vo.resp.UploadFileInfoResp;
import com.collect.backend.utils.ManageUserInfo;
import com.collect.backend.utils.assertBussion.AssertUtil;
import com.collect.backend.utils.fileType.MimeTypeUtils;
import com.collect.backend.utils.minio.MinioUtils;
import com.collect.backend.utils.redis.RedisUtils;
import io.minio.ComposeSource;
import io.minio.ObjectArgs;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.collect.backend.common.Constants.*;

@Service
@Slf4j
public class MinioUploadFileServiceImpl {

    @Autowired
    private MinioProperties minioProperties;
    @Autowired
    private MinioUtils minioUtils;
    @Autowired
    private UploadFileDao uploadFileDao;
    @Autowired
    private UserUseTotalDao userUseTotalDao;


    public String uploadFile(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String name = UUID.randomUUID().toString() ;
        String newFileName = name + MimeTypeUtils.getSuffix(originalFilename);
        String bucketName = minioProperties.getBucketName();
        minioUtils.uploadFile(bucketName,newFileName,multipartFile);
        String presignedUrl = minioUtils.getPresignedUrl(bucketName, originalFilename);
        return presignedUrl;
    }

    public boolean isExceedUseSpace(Long use,Long total){
        return use > total ? true : false;
    }

    public String recordChunkInfo(String fileMd5,Integer chunkIndex){
        return fileMd5 + "-" + chunkIndex;
    }
    public String computedUserUseSpace(Map<Object, Object> userSpaceRedisVo,
                                       String fileMd5,Integer chunkIndex,
                                       Long fileSize,Long userId){
        if(Objects.nonNull(userSpaceRedisVo.get(recordChunkInfo(fileMd5,chunkIndex)))){  //当前分片信息已经记录过
            return null;
        }
        try {
            if(isExceedUseSpace(Long.parseLong((String) userSpaceRedisVo.get(UPLOAD_FILE_USE_KEY)) + fileSize,
                    Long.parseLong((String) userSpaceRedisVo.get(UPLOAD_FILE_TOTAL_KEY)))){  //超额了，返回报错信息
                return "空间不够了";
            }
        }catch (Exception e){
            log.error("计算出错");
        }
       //更新redis使用空间信息
        System.out.println("更新前的值 "+ RedisUtils.hget(getUserUseFileKey(userId), UPLOAD_FILE_USE_KEY));
        RedisUtils.hincr(getUserUseFileKey(userId), UPLOAD_FILE_USE_KEY, fileSize);
        System.out.println("更新后的值 "+ RedisUtils.hget(getUserUseFileKey(userId), UPLOAD_FILE_USE_KEY));
        RedisUtils.hset(getUserUseFileKey(userId),recordChunkInfo(fileMd5,chunkIndex),"1");
        return null;
    }

    public Map<Object, Object> synchronousRedis(Long userId){  //同步网盘空间到缓存中
        Map<Object, Object> userSpaceRedisVo = RedisUtils.hmget(getUserUseFileKey(userId));
        if(userSpaceRedisVo.size() == 0){  //redis中没数据
            //查询数据库并存储到redis中
            UserUseTotal userUseTotalByUserId = userUseTotalDao.getUserUseTotalByUserId(userId);
            Optional.ofNullable(userUseTotalByUserId).ifPresent(m -> {
                userSpaceRedisVo.put(UPLOAD_FILE_USE_KEY,m.getUserUse().toString());
                userSpaceRedisVo.put(UPLOAD_FILE_TOTAL_KEY,m.getUserTotal().toString());
                RedisUtils.hmsetIfAbsent(getUserUseFileKey(userId),userSpaceRedisVo);
            });
        }
        AssertUtil.isTrue(userSpaceRedisVo.size() > 0,"获取用户使用空间数据失败");
        return userSpaceRedisVo;
    }
    public BaseResponse<UploadFileInfoResp> commonUploadFile(MultipartFile multipartFile, String fileMd5, Integer chunkIndex, Integer chunks) {
        if(chunkIndex >= chunks){
            return ResultUtils.error(CommonErrorEnum.BUSINESS_ERROR.getErrorCode(),"当前分片大于等于总分片");
        }
        UploadFileInfoResp uploadFileInfoResp = new UploadFileInfoResp();
        boolean fileIsSuccess = true;  // 文件是否上传成功标志，默认成功
        Long userId = ManageUserInfo.getUser().getId();
        try{
//            AtomicReference<UserSpaceRedisVo> userSpaceRedisVo = new AtomicReference<>(RedisUtils.get(getUserUseFileKey(userId), UserSpaceRedisVo.class));
            String isError = computedUserUseSpace(synchronousRedis(userId)
            , fileMd5, chunkIndex,multipartFile.getSize(),userId);
            AssertUtil.isEmpty(isError,isError);

            String bucketName = minioProperties.getBucketNameSlice();
            if(minioUtils.getFileIsExist(bucketName, minioUtils.initChunkIndexFileName(fileMd5,chunkIndex))){  //断点续传，不需要在重新上传
                uploadFileInfoResp.setFileStatus(UploadStatusEnum.UPLOADING.getStatus());
                return ResultUtils.success(uploadFileInfoResp);
            }
            Boolean isUploadSpliceFileSuccess = minioUtils.uploadSpliceFile(
                    bucketName, fileMd5, chunkIndex, multipartFile);
            if(!isUploadSpliceFileSuccess){
                return ResultUtils.error(CommonErrorEnum.BUSINESS_ERROR.getErrorCode(),"上传失败");
            }
            uploadFileInfoResp.setFileStatus(UploadStatusEnum.UPLOADING.getStatus());
            return ResultUtils.success(uploadFileInfoResp);
        }catch (Exception e){
            log.error("文件上传失败 ",e);
            System.out.println("当前有问题");
            fileIsSuccess = false;
        }finally {
            if(!fileIsSuccess){  // 失败
                // todo 利用定时任务清除文件分片碎片
            }
        }
        return ResultUtils.success(uploadFileInfoResp);
    }

    public BaseResponse<String> unionFile(UnionFileReq unionFileReq) {
        String fileMd5 = unionFileReq.getFileMd5();
        Integer chunkCount = unionFileReq.getChunkCount();
        String fileName = unionFileReq.getFileName();
        String fullObjectName = fileMd5 + MimeTypeUtils.getSuffix(fileName);
        String bucketNameSlice = minioProperties.getBucketNameSlice();
        // 完成上传从缓存目录合并迁移到正式目录
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkCount)
                .map(i -> ComposeSource.builder()
                        .bucket(bucketNameSlice)
                        .object(minioUtils.initChunkIndexFileName(fileMd5,i))
                        .build())
                .collect(Collectors.toList());

        log.debug("文件合并|composeFile|,fullObjectName:{},totalPieces:{}",fullObjectName, chunkCount);
        String bucketName = minioProperties.getBucketName();
        // 合并操作
        Boolean isCompose = minioUtils.composeFile(bucketName,fullObjectName, sourceObjectList);
        AssertUtil.isTrue(isCompose,"文件合并失败");
        List<DeleteObject> collect = sourceObjectList.stream().map(ObjectArgs::object)
                .map(DeleteObject::new).collect(Collectors.toList());
        minioUtils.removeFiles(bucketNameSlice, collect);
        return ResultUtils.success("ok");
    }


    public BaseResponse<String> deleteUploadFile(List<Long> ids) {
        Long reduceUserSpace = 0L;
        List<DeleteObject> deleteObjects = new ArrayList<>();
        String bucketName = minioProperties.getBucketName();
        for (Long id : ids) {
            UploadFile uploadFile = uploadFileDao.getBaseMapper().selectById(id);
            if(Objects.isNull(uploadFile)) continue;
            try {
                String path = uploadFile.getPath();
                long fileSize = minioUtils.getFileSize(bucketName, path);
                reduceUserSpace += fileSize;
                deleteObjects.add(new DeleteObject(path));
            } catch (Exception ignored) {
            }

        }
        minioUtils.removeFiles(bucketName,deleteObjects);
        // 更新用户使用空间
        userUseTotalDao.updateUserUploadFileSpace(reduceUserSpace,1);
        return ResultUtils.success("ok");
    }
}
