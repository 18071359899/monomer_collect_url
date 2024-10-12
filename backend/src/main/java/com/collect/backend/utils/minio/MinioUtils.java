package com.collect.backend.utils.minio;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.collect.backend.common.exception.BusinessException;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
public class MinioUtils {
    @Autowired
    private MinioClient minioClient;

    /**
     * 文件分块上传
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param sliceIndex 分片索引
     * @param file       文件
     */
    public Boolean uploadSpliceFile(String bucketName, String objectName, Integer sliceIndex, MultipartFile file) {
        try {
            if (sliceIndex != null) {
                objectName = initChunkIndexFileName(objectName,sliceIndex);
            }
            createBucket(bucketName);
            // 写入文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            log.debug("上传到minio文件|uploadFile|参数：bucketName：{}，objectName：{}，sliceIndex：{}"
                    , bucketName, objectName, sliceIndex);
            return true;
        } catch (Exception e) {
            log.error("文件上传到Minio异常|参数：bucketName:{},objectName:{},sliceIndex:{}|异常:{}", bucketName, objectName, sliceIndex, e);
            return false;
        }
    }

    /**
     * 初始化分片文件名称
     * @param fileName
     * @param chunkIndex
     * @return
     */
    public String initChunkIndexFileName(String fileName,Integer chunkIndex){
        return fileName.concat("/").concat(Integer.toString(chunkIndex));
    }

    /**
     * 文件上传
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param file       文件
     */
    public Boolean uploadFile(String bucketName, String objectName,MultipartFile file) {
        try {
            createBucket(bucketName);
            // 写入文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            log.debug("上传到minio文件|uploadFile|参数：bucketName：{}，objectName：{}"
                    , bucketName, objectName);
            return true;
        } catch (Exception e) {
            log.error("文件上传到Minio异常|参数：bucketName:{},objectName:{},异常:{}", bucketName, objectName, e);
            return false;
        }
    }

    /**
     * 创建桶，放文件使用
     *
     * @param bucketName 桶名称
     */
    public Boolean createBucket(String bucketName) {
        try {
            if (!minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            return true;
        } catch (Exception e) {
            log.error("Minio创建桶异常!|参数：bucketName:{}|异常:{}", bucketName, e);
            return false;
        }
    }

    /**
     * 文件合并
     *
     * @param bucketName       桶名称
     * @param objectName       对象名称
     * @param sourceObjectList 源文件分片数据
     */
    public Boolean composeFile(String bucketName, String objectName, List<ComposeSource> sourceObjectList) {
        // 合并操作
        try {
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .sources(sourceObjectList)
                            .build());
            return true;
        } catch (Exception e) {
            log.error("Minio文件按合并异常!|参数：bucketName:{},objectName:{}|异常:{}", bucketName, objectName, e);
            return false;
        }
    }

    /**
     * 获取文件预览url
     *
     * @param fileName 文件名
     * @return 文件预览url
     */
    public String getPresignedUrl(String bucketName,String fileName) {
        // 获取桶名
        String presignedUrl = null;
        try {
            if (StringUtils.isBlank(fileName)) {
                log.error("获取文件预览url失败，文件名为空！");
                return presignedUrl;
            }
            // 判断桶是否存在
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            // 桶存在
            if (isExist) {
                presignedUrl = minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(bucketName)
                                .object(fileName)
//                                .expiry(1, TimeUnit.DAYS) // 一天过期时间
                                .build());
                return presignedUrl;
            } else {  // 桶不存在
                log.error("获取文件预览url失败，桶{}不存在", bucketName);
            }
        } catch (Exception e) {
            log.error("获取文件预览url时出现异常: " + e.getMessage());
        }
        return presignedUrl;
    }


    /**
     * 多个文件删除
     *
     * @param bucketName 桶名称
     */
    public Boolean removeFiles(String bucketName, List<DeleteObject> delObjects) {
        try {
            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket(bucketName).objects(delObjects).build());
            boolean isFlag = true;
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("Error in deleting object {} | {}", error.objectName(), error.message());
                isFlag = false;
            }
            return isFlag;
        } catch (Exception e) {
            log.error("Minio多个文件删除异常!|参数：bucketName:{},objectName:{}|异常:{}", bucketName, e);
            return false;
        }
    }

    /**
     * 获取文件大小
     */
    public long getFileSize(String bucketName,String fileName) throws Exception {
        try {
            StatObjectResponse fileInfo = getFileInfo(bucketName, fileName);
            if (fileInfo != null) {
                return fileInfo.size();
            }
            return 0L;
        } catch (Exception e) {
            log.error("获取文件大小失败:{}", e.getMessage(), e);
            throw new BusinessException("文件不存在或信息有误，获取文件失败");
        }
    }


    /**
     * 检测文件是否存在
     */
    public boolean getFileIsExist(@NonNull String bucketName, String fileName) throws Exception {
        boolean exist = true;
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }
    /**
     * 获取文件信息
     */
    public StatObjectResponse getFileInfo(@NonNull String bucketName, String fileName) throws Exception {
        try {
            return minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 判断文件夹是否存在
     *
     * @param bucketName 存储桶
     * @param objectName 文件夹名称
     * @return
     */
    public boolean isFolderExist(String bucketName, String objectName) {
        boolean exist = false;
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).prefix(objectName).recursive(false).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir() && objectName.equals(item.objectName())) {
                    exist = true;
                }
            }
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    /**
     * 获取文件流
     *
     * @param bucketName 存储桶
     * @param objectName 文件名
     * @return 二进制流
     */
    public InputStream getObject(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 断点下载
     *
     * @param bucketName 存储桶
     * @param objectName 文件名称
     * @param offset     起始字节的位置
     * @param length     要读取的长度
     * @return 二进制流
     */
    public InputStream getObject(String bucketName, String objectName, long offset, long length) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .offset(offset)
                        .length(length)
                        .build());
    }

    /**
     * 获取路径下文件列表
     *
     * @param bucketName 存储桶
     * @param prefix     文件名称
     * @param recursive  是否递归查找，false：模拟文件夹结构查找
     * @return 二进制流
     */
    public Iterable<Result<Item>> listObjects(String bucketName, String prefix,
                                              boolean recursive) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .recursive(recursive)
                        .build());
    }

}