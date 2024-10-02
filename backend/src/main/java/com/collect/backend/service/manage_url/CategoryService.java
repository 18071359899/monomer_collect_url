package com.collect.backend.service.manage_url;

import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.entity.User;
import com.collect.backend.domain.vo.req.DeleteListByIds;
import com.collect.backend.domain.vo.resp.NodeCategoryVo;

import java.util.Date;
import java.util.List;

/**
* @author Lenovo
* @description 针对表【category(给网站做分类的表)】的数据库操作Service
* @createDate 2024-02-01 22:50:29
*/
public interface CategoryService{

    BaseResponse<List<Category>> listCategory(User user);

    BaseResponse<List<Category>> getCategory(User user, Long pid);

    BaseResponse<Category> addCategory(Category category);

    BaseResponse<String> updateCategory(Category category);

    BaseResponse<String> deleteCategory(List<Long> ids);
    void copyCategory(List<Category> categories,Long userId,Long pid, Date date);

    BaseResponse<List<NodeCategoryVo>> getTreeNodeCategory(User user, Long pid);
}
