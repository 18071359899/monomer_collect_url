package com.collect.backend.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.domain.entity.Website;
import com.collect.backend.mapper.FileMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.collect.backend.common.enums.LogicDeleteTypeEnum.NO_DELETE;
import static com.collect.backend.common.enums.LogicDeleteTypeEnum.YES_DELETE;

@Service
public class UploadFileDao extends ServiceImpl<FileMapper, UploadFile> {
    public UploadFile queryByMd5(String md5){
        List<UploadFile> list = lambdaQuery().eq(UploadFile::getMd5, md5).list();
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 查询用户的某个目录下的所有文件
     * @param userId
     * @param pid
     * @return
     */
    public List<UploadFile> getFileByPidUserId(Long userId,Long pid){
        return lambdaQuery().eq(UploadFile::getUserId, userId)
                .eq(UploadFile::getIsDelete,NO_DELETE.getType()).eq(UploadFile::getPid,pid).list();
    }

    /**
     * 获取逻辑删除的数据
     * @param userId
     * @param pid
     * @return
     */

    public List<UploadFile> getDeleteFileByPidUserId(Long userId,Long pid){
        return lambdaQuery().eq(UploadFile::getUserId, userId)
                .eq(UploadFile::getIsDelete,YES_DELETE.getType()).eq(UploadFile::getPid,pid).list();
    }

    /**
     * 查询当前用户某个类型文件的全部数据
     * @param userId
     * @param type
     * @return
     */
    public List<UploadFile> getFileByUserIdType(Long userId,Integer type){
        return lambdaQuery().eq(UploadFile::getUserId, userId)
                .eq(UploadFile::getIsDelete,NO_DELETE.getType()).
                eq(UploadFile::getFileType,type).list();
    }

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    public boolean logicDelete(Long id) {
        return lambdaUpdate().eq(UploadFile::getId,id)
                .set(UploadFile::getIsDelete,1)
                .update();
    }

    /**
     * 获取逻辑删除的数据
     * @param id
     * @return
     */
    public UploadFile getDeleteData(Long id) {
        return lambdaQuery()
                .eq(UploadFile::getIsDelete,YES_DELETE.getType()).eq(UploadFile::getId,id).one();
    }

    /**
     * 物理删除
     * @param id
     */
    public void removeData(Long id) {
        baseMapper.deleteById(id);
    }

    /**
     * 还原文件，逻辑恢复
      * @param id
     * @return
     */
    public boolean reductionData(Long id) {
        return lambdaUpdate().eq(UploadFile::getId, id).set(UploadFile::getIsDelete, NO_DELETE.getType())
                .update();
    }
}
