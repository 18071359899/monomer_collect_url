package com.collect.backend.controller.manage_data;


import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.vo.req.DeleteListByIds;
import com.collect.backend.domain.vo.resp.NodeCategoryVo;
import com.collect.backend.service.manage_url.CategoryService;
import com.collect.backend.utils.ManageUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryContrller {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/list/")
    public BaseResponse<List<Category>> listCategory(){
        return categoryService.listCategory(ManageUserInfo.getUser());
    }
    @GetMapping("/get/")
    public BaseResponse<List<Category>> getCategory(Long pid){
        return categoryService.getCategory(ManageUserInfo.getUser(),pid);
    }

    @GetMapping("/node/")
    public BaseResponse<List<NodeCategoryVo>> getTreeNodeCategory(Long pid){
        return categoryService.getTreeNodeCategory(ManageUserInfo.getUser(),pid);
    }

    @PostMapping("/add/")
    public BaseResponse<Category> addCategory(@Valid @RequestBody  Category category){
        return categoryService.addCategory(category);
    }

    @PostMapping("/update/")
    public BaseResponse<String> updateCategory(@Valid @RequestBody  Category category){
        return categoryService.updateCategory(category);
    }

    @PostMapping("/delete/")
    public BaseResponse<String> deleteCategory(@Valid @RequestBody DeleteListByIds ids){
        return categoryService.deleteCategory(ids.getIds());
    }

}
