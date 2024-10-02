package com.collect.backend.mq_consumer;

import com.collect.backend.common.enums.FileTypeEnum;
import com.collect.backend.dao.UploadFileDao;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.service.impl.upload_file.UploadVideoChunk;
import com.collect.backend.utils.fileType.MimeTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

import static com.collect.backend.service.impl.upload_file.UploadFileServiceImpl.HEIGHT;
import static com.collect.backend.service.impl.upload_file.UploadFileServiceImpl.WIDTH;

@Slf4j
@Component
public class MqListener {

    @Autowired
    private UploadVideoChunk uploadVideoChunk;
    @Autowired
    private UploadFileDao uploadFileDao;
    @Value("${myfile.path}")
    private String filePath;
    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue2(Map<String, Object> msg) throws InterruptedException {
        String monthDay = (String) msg.get("monthDay");
        String realFileName = (String) msg.get("realFileName");
        String targetFileName = (String) msg.get("targetFileName");
        String  fileName = (String) msg.get("fileName");
        Long id = Long.parseLong((String) msg.get("id"));
        String suffixNoPoint = MimeTypeUtils.getSuffixNoPoint(fileName);

        Integer fileType = MimeTypeUtils.getFileTypeByFileSuffix(suffixNoPoint).getType();

        String thumbnailPath = monthDay + "/" + MimeTypeUtils.getPrefix(realFileName) + "-thumbnail"  + ".jpg";

        UploadFile uploadFile = new UploadFile();
        uploadFile.setId(id);

        if(fileType.equals(FileTypeEnum.MOVIE_TYPE.getType())){  //如果是视频，进行视频的分割
            try {
                uploadVideoChunk.uploadVideoToM3U8(targetFileName,
                        monthDay + "/" + MimeTypeUtils.getPrefix(realFileName),MimeTypeUtils.getPrefix(realFileName));
            }catch (Throwable e){
                log.error("视频转码失败");
            }
            String result = null;
            try {
                result = uploadVideoChunk.generateVideoThumbnail(targetFileName, filePath +
                        thumbnailPath, WIDTH, HEIGHT);
            } catch (Throwable e) {
                log.error("生成缩略图失败");
            }
            if(Objects.nonNull(result)){  // 处理转缩略图失败逻辑
                thumbnailPath = "default/image-default.png";
            }
        }
        else if(fileType.equals(FileTypeEnum.IMG_TYPE.getType())){ //如果是图片，生成缩略图
            String result = null;
            try {
                result = uploadVideoChunk.generateImageThumbnail(targetFileName, filePath +
                        thumbnailPath, WIDTH, HEIGHT);
            } catch (Throwable e) {
                log.error("生成缩略图失败");
            }
            if(Objects.nonNull(result)){  // 处理转缩略图失败逻辑
                thumbnailPath = "default/video-default.png";
            }
        }
        else{   //其他类型文件的图片，根据文件后缀名对应的默认图片即可
            thumbnailPath = MimeTypeUtils.getFileDefaultByFileType(suffixNoPoint);
        }
        uploadFile.setThumbnail(thumbnailPath);
        uploadFileDao.getBaseMapper().updateById(uploadFile);
        System.out.println("处理完成");
    }
}
