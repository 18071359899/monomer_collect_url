package com.collect.backend.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.entity.Website;
import com.collect.backend.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.collect.backend.common.enums.LogicDeleteTypeEnum.NO_DELETE;
import static com.collect.backend.common.enums.LogicDeleteTypeEnum.YES_DELETE;

@Service
public class CategoryDao extends ServiceImpl<CategoryMapper, Category> {

    public List<Category> listByUserId(Long userId){
        return lambdaQuery().eq(Category::getUserId, userId)
                .eq(Category::getIsDelete,NO_DELETE.getType())
                .list();
    }

    public List<Category> getByPid(Long userId, Long pid) {
        return lambdaQuery().eq(Category::getUserId, userId)
                .eq(Category::getIsDelete,NO_DELETE.getType()).eq(Category::getPid,pid).list();
    }

    //获取所有数据，不包括 is_delete 字段条件
    public List<Category> getAllByPidNoIsDelete(Long userId, Long pid) {
        return lambdaQuery().eq(Category::getUserId, userId)
                    .eq(Category::getPid,pid).list();
    }

    public boolean logicDelete(Long id) {
        return lambdaUpdate().eq(Category::getId,id)
                .set(Category::getIsDelete,YES_DELETE.getType())
                .update();
    }

    //获取删除数据的信息
    public Category getDeleteData(Long id) {
        return lambdaQuery()
                .eq(Category::getIsDelete,YES_DELETE.getType()).eq(Category::getId,id).one();
    }

    //还原分类
    public boolean reductionData(Long id) {
        return lambdaUpdate().eq(Category::getId, id)
                .set(Category::getIsDelete, NO_DELETE.getType()).update();
    }
}
