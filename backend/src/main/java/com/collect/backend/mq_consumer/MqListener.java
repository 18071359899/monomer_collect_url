package com.collect.backend.mq_consumer;

import com.collect.backend.common.enums.FileTypeEnum;
import com.collect.backend.common.enums.MessageNotifyTypeEnum;
import com.collect.backend.common.enums.UserShareBehaviorISAddType;
import com.collect.backend.dao.*;
import com.collect.backend.domain.entity.MessageNotify;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.domain.entity.mq_event.LikeQueueEntity;
import com.collect.backend.service.impl.upload_file.UploadVideoChunk;
import com.collect.backend.utils.fileType.MimeTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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


    @Autowired
    private ShareDao shareDao;

    @Autowired
    private MessageNotifyDao messageNotifyDao;
    @Autowired
    private UserShareBehaviorDao shareBehaviorDao;
    @Autowired
    private UserAgreeShareDao userAgreeShareDao;
    /**
     * 内存中聚合用户点赞总数： 文章id对应文章要增加的次数，负数为减少
     */

    private static ConcurrentHashMap<Long,Long> recordUserLikeCounts = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long,Long> recordUserLikeCounts2 = new ConcurrentHashMap<>();
    /**
     * 处理视频、图片相关的内容
     * @param msg
     * @throws InterruptedException
     */
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
    }

    public void setMessageNotify(Share share,Long userId){
        MessageNotify messageNotify = new MessageNotify(null,
                share.getUserId(), userId,null,share.getId(), false,new Date());
        messageNotify.setType(MessageNotifyTypeEnum.USER_AGREE_ARTICLE.getType());
        if(Objects.isNull(messageNotify.getAcceptUserId()) && Objects.isNull(messageNotify.getSendUserId())) return;
        if(!messageNotify.getAcceptUserId().equals(messageNotify.getSendUserId())) //收发信息的人不能是同一个
            messageNotifyDao.getBaseMapper().insert(messageNotify);
    }

    public void handleLike(LikeQueueEntity likeQueueEntity){
        //todo 筛选重复数据
        Long userId = likeQueueEntity.getUserId();
        Share share = shareDao.getBaseMapper().selectById(likeQueueEntity.getRelationId());
        if(Objects.isNull(share)) return;
        Long shareId = share.getId();
        Long likeCountsOrDefault = 0L;
        //记录用户对于点赞的行为
        if(likeQueueEntity.getIsAdd() == UserShareBehaviorISAddType.ADD_TYPE.getType()){
            shareBehaviorDao.getBaseMapper().insert(likeQueueEntity);
            likeCountsOrDefault = 1L;
        }else if(likeQueueEntity.getIsAdd() == UserShareBehaviorISAddType.DELETE_TYPE.getType()){
            shareBehaviorDao.getBaseMapper().deleteById(
                    shareBehaviorDao.queryByUserIdAndTypeAndShareId(userId, likeQueueEntity.getType(), shareId));
            likeCountsOrDefault = -1L;
        }
        Long count = recordUserLikeCounts.getOrDefault(shareId, 0L) + likeCountsOrDefault; //2. 同时获取到数据
        System.out.println("更新的值 "+count);
        //在内存中记录要增加的点赞数
        recordUserLikeCounts.put(shareId,count);

        //消息通知到点赞对应的文章用户
        setMessageNotify(share,userId);
    }
    public void handleLike2(LikeQueueEntity likeQueueEntity){
        Long userId = likeQueueEntity.getUserId();
        Share share = shareDao.getBaseMapper().selectById(likeQueueEntity.getRelationId());
        if(Objects.isNull(share)) return;
        Long shareId = share.getId();
        Long likeCountsOrDefault = 0L;
        //记录用户对于点赞的行为
        if(likeQueueEntity.getIsAdd() == UserShareBehaviorISAddType.ADD_TYPE.getType()){
            shareBehaviorDao.getBaseMapper().insert(likeQueueEntity);
            likeCountsOrDefault = 1L;
        }else if(likeQueueEntity.getIsAdd() == UserShareBehaviorISAddType.DELETE_TYPE.getType()){
            shareBehaviorDao.getBaseMapper().deleteById(
                    shareBehaviorDao.queryByUserIdAndTypeAndShareId(userId, likeQueueEntity.getType(), shareId));
            likeCountsOrDefault = -1L;
        }
        //在内存中记录要增加的点赞数
        recordUserLikeCounts2.put(shareId,recordUserLikeCounts2.getOrDefault(shareId, 0L) +
                likeCountsOrDefault);

        //消息通知到点赞对应的文章用户
        setMessageNotify(share,userId);
    }
    @RabbitListener(queues = "direct.likeQueue")
    public void listenLikeQueue(LikeQueueEntity likeQueueEntity){
        System.out.println("消费者1");
        handleLike(likeQueueEntity);
    }


    @RabbitListener(queues = "direct.likeQueue")
    public void listenLikeQueue2(LikeQueueEntity likeQueueEntity){
        System.out.println("消费者2");
        handleLike2(likeQueueEntity);
    }


    @Async("threadPoolTaskExecutor")
    @Scheduled(cron ="0/10 * * * * ? ")
    public void updateLikeCountToDb(){  //每十秒聚合更新点赞数到db中，聚合成功后清楚对应记录
        Iterator<Map.Entry<Long, Long>> iterator = recordUserLikeCounts.entrySet().iterator();
        System.out.println("更新中，数据： " + recordUserLikeCounts.toString());
        while (iterator.hasNext()){
            System.out.println("有数据");
            Map.Entry<Long, Long> item = iterator.next();  //1.
            iterator.remove();
            Long shareId = item.getKey();
            Long count = item.getValue();
            userAgreeShareDao.updateAgreeCount(shareId,count);
//            recordUserLikeCounts.remove(shareId);
        }
    }

    @Async("threadPoolTaskExecutor")
    @Scheduled(cron ="0/10 * * * * ? ")
    public void updateLikeCountToDb2(){  //每十秒聚合更新点赞数到db中，聚合成功后清楚对应记录
        Iterator<Map.Entry<Long, Long>> iterator = recordUserLikeCounts2.entrySet().iterator();
        System.out.println("更新中，数据： " + recordUserLikeCounts2.toString());
        while (iterator.hasNext()){
            Map.Entry<Long, Long> item = iterator.next();
            iterator.remove();
            System.out.println("已经删除");
            Long shareId = item.getKey();
            Long count = item.getValue();
            userAgreeShareDao.updateAgreeCount(shareId,count);
        }
    }

}
