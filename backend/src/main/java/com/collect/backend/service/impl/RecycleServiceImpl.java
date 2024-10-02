package com.collect.backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.enums.DirectoryTypeEnum;
import com.collect.backend.common.event.RecycleEvent;
import com.collect.backend.dao.RecycleDao;
import com.collect.backend.dao.WebsiteDao;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.entity.Recycle;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.domain.entity.Website;
import com.collect.backend.domain.vo.resp.RecycleVo;
import com.collect.backend.service.impl.upload_file.UploadFileServiceImpl;
import com.collect.backend.service.manage_url.RecycleService;
import com.collect.backend.service.impl.manage_data.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
* @author Lenovo
* @description 针对表【recycle(回收站表)】的数据库操作Service实现
* @createDate 2024-02-16 22:24:52
*/
@Service
public class RecycleServiceImpl implements RecycleService{
    @Autowired
    private RecycleDao recycleDao;
    @Autowired
    private WebsiteDao websiteDao;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private UploadFileServiceImpl uploadFileService;

    @Override
    public Page<RecycleVo> listRecycle(long page, long size){
        Page<Recycle> pagination = new Page<>(page,size);
        Page<Recycle> recyclePage = recycleDao.getBaseMapper().selectPage(pagination, null);
        Page<RecycleVo> recycleVoPage = new Page<>(page,size,recyclePage.getTotal());
        List<Recycle> records = recyclePage.getRecords();
        List<RecycleVo> recycleVoList = new ArrayList<>();
        records.forEach(value->{
            RecycleVo recycleVo = new RecycleVo();
            BeanUtil.copyProperties(value,recycleVo);
            DirectoryTypeEnum directoryEnum = DirectoryTypeEnum.getByType(value.getType());
            switch (directoryEnum){
               //文件夹
                case CATEGORY:
                   Category category = categoryService.getCategoryDao().getDeleteData(value.getDeleteId());
                   if(Objects.nonNull(category))
                   recycleVo.setName(category.getName());
                   break;
               //网址
               case WEBSITE:
                   Website website = websiteDao.getDeleteData(value.getDeleteId());
                   if(Objects.nonNull(website)){
                       recycleVo.setTitle(website.getTitle());
                       recycleVo.setIcon(website.getIcon());
                   }
                   break;
                case UPLOAD_FILE:
                    UploadFile uploadFile = uploadFileService.getUploadFileDao().getDeleteData(value.getDeleteId());
                    if(Objects.nonNull(uploadFile)){
                        recycleVo.setTitle(uploadFile.getFileName());
                        recycleVo.setIcon(uploadFile.getThumbnail());
                    }
                    break;
               default: break;
           }
           recycleVoList.add(recycleVo);
        });
        recycleVoPage.setRecords(recycleVoList);
        return recycleVoPage;
    }

    @Override
    public String removeDataRecycle(List<RecycleVo> recycleVoList) {
        recycleVoList.forEach(recycleVo -> {
            Long id = recycleVo.getId();
            Long deleteId = recycleVo.getDeleteId();
            DirectoryTypeEnum directoryEnum = DirectoryTypeEnum.getByType(recycleVo.getType());
            recycleDao.getBaseMapper().deleteById(id);
            switch (directoryEnum){
                case  CATEGORY:
                    //物理删除该分类所有数据，解耦
                    applicationEventPublisher.publishEvent(new RecycleEvent(this,recycleVo));
                    break;
                case  WEBSITE:
                    websiteDao.removeData(deleteId);
                    break;
                case  UPLOAD_FILE:
                    List<Long> ids = new ArrayList<>();
                    ids.add(deleteId);
                    uploadFileService.deleteUploadFile(ids);
                    break;
            }
        });

        return "ok";
    }

    @Override
    public String reductionDataRecycle(List<RecycleVo> recycleVoList) {
        recycleVoList.forEach(recycleVo -> {
            Long id = recycleVo.getId();
            Long deleteId = recycleVo.getDeleteId();
            DirectoryTypeEnum directoryEnum = DirectoryTypeEnum.getByType(recycleVo.getType());
            recycleDao.getBaseMapper().deleteById(id);
            switch (directoryEnum){
                case  CATEGORY:
                    categoryService.getCategoryDao().reductionData(deleteId);
                    break;
                case  WEBSITE:
                   websiteDao.reductionData(deleteId);
                    break;
                case UPLOAD_FILE:
                    uploadFileService.getUploadFileDao().reductionData(deleteId);
                    break;
            }
        });
        return "ok";
    }
}




