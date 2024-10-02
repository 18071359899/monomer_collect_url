package com.collect.backend.common.event.listener;


import com.collect.backend.common.event.RecycleEvent;
import com.collect.backend.common.event.DeleteDataEvent;
import com.collect.backend.dao.CategoryDao;
import com.collect.backend.dao.RecycleDao;
import com.collect.backend.dao.WebsiteDao;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.entity.Recycle;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.domain.entity.Website;
import com.collect.backend.domain.vo.resp.RecycleVo;
import com.collect.backend.domain.vo.resp.directory.CommonDirectory;
import com.collect.backend.service.impl.upload_file.UploadFileServiceImpl;
import com.collect.backend.utils.ManageUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 监听回收站相关事件，进入回收站，删除回收站内容
 */
@Component
public class RecycleListener {
    @Autowired
    private WebsiteDao websiteDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private RecycleDao recycleDao;
    @Autowired
    private UploadFileServiceImpl uploadFileService;

    public void deleteDatById(Long id,Long userId){
        categoryDao.getBaseMapper().deleteById(id);
        List<Website> websites = websiteDao.getAllByPidNoIsDelete(userId, id);
        uploadFileService.deleteUploadFile(uploadFileService.getUploadFileDao().getDeleteFileByPidUserId(userId,id).stream()
                .map(UploadFile::getId).collect(Collectors.toList()));
        //删除该目录下网址的所有信息
        websites.forEach(website -> {
            Long webSiteId = website.getId();
            websiteDao.getBaseMapper().deleteById(webSiteId);
        });
        List<Category> categories = categoryDao.getAllByPidNoIsDelete(userId, id);
        categories.forEach(e->deleteDatById(e.getId(),userId));
    }

    //物理删除： 递归删除该分类以及该分类下的所有数据
    @EventListener(classes = RecycleEvent.class)
    @Async("threadPoolTaskExecutor")
    public void removeData(RecycleEvent recycleEvent){
        RecycleVo recycleVo = recycleEvent.getRecycleVo();
        deleteDatById(recycleVo.getDeleteId(),ManageUserInfo.getUser().getId());
    }

    //用户点击删除后进入回收站
    @EventListener(classes = DeleteDataEvent.class)
    @Async("threadPoolTaskExecutor")
    public void toSaveRecycle(DeleteDataEvent deleteDataEvent){
        List<CommonDirectory> directoryVos = deleteDataEvent.getDirectoryVos();
        Date date = new Date();
        directoryVos.forEach(directoryVo->{
            if(Objects.nonNull(directoryVo)){
                recycleDao.getBaseMapper().insert(new Recycle(
                        null,directoryVo.getType(),directoryVo.getId(),date
                ));
            }
        });
    }
}
