package com.collect.backend.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.entity.Website;
import com.collect.backend.mapper.WebsiteMapper;
import com.collect.backend.service.manage_url.WebsiteService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.collect.backend.common.enums.LogicDeleteTypeEnum.NO_DELETE;
import static com.collect.backend.common.enums.LogicDeleteTypeEnum.YES_DELETE;

/**
* @author Lenovo
* @description 针对表【website】的数据库操作Service实现
* @createDate 2024-02-06 21:10:39
*/
@Service
public class WebsiteDao extends ServiceImpl<WebsiteMapper, Website>{
    /**
     * 获取当前用户的所有网址
     * @param userId
     * @return
     */
    public List<Website> getAllByUserId(Long userId) {
        return lambdaQuery().
                eq(Website::getUserId,userId)
                .eq(Website::getIsDelete,NO_DELETE.getType())
                .list();
    }


    public List<Website> getAllByPid(Long pid, Long userId) {
        return lambdaQuery().
                eq(Website::getUserId,userId)
                .eq(Website::getPid,pid)
                .eq(Website::getIsDelete,NO_DELETE.getType())
                .list();
    }

    public boolean logicDelete(Long id) {
        return lambdaUpdate().eq(Website::getId,id)
                .set(Website::getIsDelete,YES_DELETE.getType())
                .update();
    }
    //获取删除数据的信息
    public Website getDeleteData(Long id) {
        return lambdaQuery()
                .eq(Website::getIsDelete,YES_DELETE.getType()).eq(Website::getId,id).one();
    }

    //删除网址，物理删除
    public void removeData(Long id) {
        baseMapper.deleteById(id);
    }
    //还原网址，逻辑恢复
    public boolean reductionData(Long id) {
        return lambdaUpdate().eq(Website::getId, id).set(Website::getIsDelete, NO_DELETE.getType())
                .update();
    }

    public List<Website> getAllByPidNoIsDelete(Long userId, Long pid) {
        return lambdaQuery().eq(Website::getUserId, userId)
                .eq(Website::getPid,pid).list();
    }
}




