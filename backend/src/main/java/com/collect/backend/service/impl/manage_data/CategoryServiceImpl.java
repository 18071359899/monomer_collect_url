package com.collect.backend.service.impl.manage_data;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ErrorCode;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.common.enums.LogicDeleteTypeEnum;
import com.collect.backend.common.exception.CommonErrorEnum;
import com.collect.backend.dao.CategoryDao;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.entity.User;
import com.collect.backend.domain.vo.resp.NodeCategoryVo;
import com.collect.backend.service.directory.DirectoryAdapter;
import com.collect.backend.service.manage_url.CategoryService;
import com.collect.backend.utils.assertBussion.AssertUtil;
import com.collect.backend.utils.ManageUserInfo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author Lenovo
* @description 针对表【category(给网站做分类的表)】的数据库操作Service实现
* @createDate 2024-02-01 22:50:29
*/
@Service
@Data
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public BaseResponse<List<Category>> listCategory(User user) {
        return ResultUtils.success(categoryDao.listByUserId(user.getId()));
    }

    @Override
    public BaseResponse<List<Category>> getCategory(User user, Long pid) {
        return ResultUtils.success(categoryDao.getByPid(user.getId(),pid));
    }
    @Override
    public BaseResponse<Category> addCategory(Category category) {
        Long pid = category.getPid();
        String name = category.getName();
        Long userId = ManageUserInfo.getUser().getId();
        List<Category> categories = categoryDao.getByPid(userId, category.getPid());
        //如果重复，加上当前时间表示唯一性
        String newName = DirectoryAdapter.searchCategoryNameIsRepeat(name,categories);
        if(StringUtils.isNotBlank(newName)){
            name = newName;
        }
        Date date = new Date();
        Category newCategory = new Category(null,
                pid, name, userId, 0, date, date);
        int insert = categoryDao.getBaseMapper().insert(newCategory);
        if(insert > 0){
            return ResultUtils.success(newCategory);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR);
    }

    @Override
    public BaseResponse<String> updateCategory(Category category) {
        Long id = category.getId();
        if(Objects.isNull(id)){
            return ResultUtils.error(CommonErrorEnum.PARAM_INVALID.getCode(),"id不能为空");
        }
        Long pid = category.getPid();
        String name = category.getName();
        Long userId = ManageUserInfo.getUser().getId();
        //将自身去掉
        List<Category> categories = categoryDao.getByPid(userId, category.getPid()).stream().
                filter((category1 -> !category1.getId().equals(id))).collect(Collectors.toList());
        //如果重复，加上当前时间表示唯一性
        String newName = DirectoryAdapter.searchCategoryNameIsRepeat(name,categories);
        if(StringUtils.isNotBlank(newName)){
            name = newName;
        }
        Date date = new Date();
        Category updateCategory = new Category(id,
                pid, name, userId, 0, null, date);
        int update = categoryDao.getBaseMapper().updateById(updateCategory);
        if(update > 0){
            return ResultUtils.success(name);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR);
    }

    @Override
    public BaseResponse<String> deleteCategory(List<Long> ids) {
        for(Long id: ids){
            categoryDao.logicDelete(id);
        }
        return ResultUtils.success("ok");
    }

    @Override
    public void copyCategory(List<Category> categories,Long userId,Long pid,Date date) {
        //查询复制目录的所有分类
        List<Category> categoriesByPid = categoryDao.getByPid(userId, pid);
        categories.forEach(value->{
            value.setPid(pid);value.setId(null);value.setUserId(userId);
            value.setCreateTime(date); value.setUpdateTime(date);
            value.setIsDelete(LogicDeleteTypeEnum.NO_DELETE.getType());
            String newName = DirectoryAdapter.searchCategoryNameIsRepeat(value.getName(), categoriesByPid);
            if(StringUtils.isNotBlank(newName)){
                value.setName(newName);
            }
            categoryDao.getBaseMapper().insert(value);
        });
    }

    @Override
    public BaseResponse<List<NodeCategoryVo>> getTreeNodeCategory(User user, Long pid) {
        AssertUtil.isNotEmpty(pid,"父级目录不能为空");
        Long userId = user.getId();
        List<Category> categories = categoryDao.getByPid(userId, pid);
        List<NodeCategoryVo> nodeCategoryVos = new ArrayList<>();
        categories.forEach(category -> {
            NodeCategoryVo nodeCategoryVo = new NodeCategoryVo();
            nodeCategoryVo.setId(category.getId());
            nodeCategoryVo.setLabel(category.getName());
            //判断该分类下是否还有分类，跟前端leaf属性对应
            nodeCategoryVo.setLeaf(categoryDao.getByPid(userId,category.getId()).size() > 0 ? false : true);
            nodeCategoryVos.add(nodeCategoryVo);
        });
        return ResultUtils.success(nodeCategoryVos);
    }


    //判断目标目录是否是原目录的子目录或本身目录
    public boolean judgeTargetDirectoryIsSourceChildren(Long source,Long target,Long userId){
        if(source.equals(target)){
            return true;
        }
        List<Long> newSoureces = categoryDao.getByPid(userId, source).stream().map(Category::getId).collect(Collectors.toList());
        for(int i = 0;i < newSoureces.size(); i++){
            if(judgeTargetDirectoryIsSourceChildren(newSoureces.get(i),target,userId)){
                return true;
            }
        }
        return  false;
    }
    public String cutCategory(List<Category> categories,Date date,Long userId, Long target) {
        //查询复制目录的所有分类
        List<Category> categoriesByPid = categoryDao.getByPid(userId, target);
        for (Category value : categories) {
            Long source = value.getId();
            if(judgeTargetDirectoryIsSourceChildren(source,target,userId)){
                return "不能复制到该目录或该目录的子目录";
            }
            value.setPid(target);
            value.setUpdateTime(date);
            String newName = DirectoryAdapter.searchCategoryNameIsRepeat(value.getName(), categoriesByPid);
            if(StringUtils.isNotBlank(newName)){
                value.setName(newName);
            }
            categoryDao.getBaseMapper().updateById(value);
        }
        return null;
    }
}




