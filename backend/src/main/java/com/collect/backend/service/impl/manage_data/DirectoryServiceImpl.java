package com.collect.backend.service.impl.manage_data;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.common.enums.DirectoryTypeEnum;
import com.collect.backend.common.enums.LogicDeleteTypeEnum;
import com.collect.backend.common.event.DeleteDataEvent;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.domain.entity.Website;
import com.collect.backend.domain.vo.req.PasteToDiectoryReq;
import com.collect.backend.domain.vo.resp.directory.CommonDirectory;
import com.collect.backend.domain.vo.resp.directory.DetailsDirectoryVo;
import com.collect.backend.domain.vo.resp.directory.DirectoryPositionResp;
import com.collect.backend.service.directory.DirectoryAdapter;
import com.collect.backend.service.impl.upload_file.UploadFileServiceImpl;
import com.collect.backend.utils.assertBussion.AssertUtil;
import com.collect.backend.utils.ManageUserInfo;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DirectoryServiceImpl {
    @Autowired
    private WebsiteServiceImpl websiteService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private UploadFileServiceImpl uploadFileService;
    @Autowired
    private Client client;
    // 定义回调函数接口
    interface Callback {
        void onCallback(Category category);
    }
    public void getPositionByPid(Long pid,Callback callback){
        Queue<Category> queue = new LinkedList<>();
        Category category = Optional.ofNullable(
                        categoryService.getCategoryDao().getBaseMapper().selectById(pid))
                .orElse(new Category(null,0L,null,null,null,null,null));
        queue.offer(category);
        //使用队列来找到当前文件夹或网址的位置
        while (!queue.isEmpty()){
            Category currDetailsDirectoryVo = queue.poll();
            Long currPid = currDetailsDirectoryVo.getPid();
            callback.onCallback(currDetailsDirectoryVo);
            if(currPid.equals(0L)){         //找到根了
                continue;
            }
            Category newCategory = categoryService.getCategoryDao().getBaseMapper().selectById(currPid);
            queue.offer(newCategory);
        }
    }
    public List<CommonDirectory> getDirectoryVoList(ArrayList<HashMap<String, Object>> Hits, Integer type){
        List<CommonDirectory> directoryVos = new ArrayList<>();
        for (HashMap<String, Object> hit : Hits) {
            CommonDirectory directoryVo = JSONObject.toJavaObject(new JSONObject(hit), CommonDirectory.class);
            directoryVo.setType(type);
            directoryVos.add(directoryVo);
        }
        return directoryVos;
    }
    public BaseResponse<DetailsDirectoryVo> detailsDirectoryVoBaseResponse(@PathVariable("id") Long id, @PathVariable("type") Integer type){
        DetailsDirectoryVo detailsDirectoryVo = new DetailsDirectoryVo();
        if(DirectoryTypeEnum.CATEGORY.getType().equals(type)){
            Category category = categoryService.getCategoryDao().getBaseMapper().selectById(id);
            DirectoryAdapter.setCategoryResp(category,detailsDirectoryVo);
        }else if(DirectoryTypeEnum.WEBSITE.getType().equals(type)){
            Website website = websiteService.getWebsiteDao().getBaseMapper().selectById(id);
            DirectoryAdapter.setWebsiteResp(website,detailsDirectoryVo);
        }
        else if(DirectoryTypeEnum.UPLOAD_FILE.getType().equals(type)){
            UploadFile uploadFile = uploadFileService.getUploadFileDao().getBaseMapper().selectById(id);
            DirectoryAdapter.setUploadFileResp(uploadFile,detailsDirectoryVo);
        }
        StringBuilder stringBuilder = new StringBuilder();
        getPositionByPid(detailsDirectoryVo.getPid(), new Callback() {
            @Override
            public void onCallback(Category category) {
                String name = category.getName();
                if(StringUtils.isNotBlank(name))
                    stringBuilder.insert(0, name +"/");
                if(category.getPid().equals(0L)){
                    stringBuilder.insert(0,  "全部文件/");
                    stringBuilder.deleteCharAt(stringBuilder.length()-1);
                }
            }
        });
        detailsDirectoryVo.setPosition(stringBuilder.toString());
        return ResultUtils.success(detailsDirectoryVo);
    }

    public BaseResponse<List<CommonDirectory>> searchDirectory(String search){
        AssertUtil.isNotEmpty(search,"搜索条件为空");
        Long userId = ManageUserInfo.getUser().getId();
        Index categoryIndex = client.index("category");
        Index websiteIndex = client.index("website");


        String[] Filter = new String[]{"isDelete=0","userId="+userId}; //user_id  IN [1]
        String[] categoryStrs = new String[]{"name"};
        SearchResult categorySearchResult = (SearchResult) categoryIndex.search(new SearchRequest(search)
                .setFilter(Filter)
                .setAttributesToSearchOn(categoryStrs));

        String[] websiteStrs = new String[]{"title","describe"};

        SearchResult websiteSearchResult = (SearchResult) websiteIndex.search(new SearchRequest(search)
                .setFilter(Filter)
                .setAttributesToSearchOn(websiteStrs));

        ArrayList<HashMap<String, Object>> categoryHits = categorySearchResult.getHits();
        ArrayList<HashMap<String, Object>> websiteHits = websiteSearchResult.getHits();


        List<CommonDirectory> directoryVoList = getDirectoryVoList(categoryHits,DirectoryTypeEnum.CATEGORY.getType());
        directoryVoList.addAll(getDirectoryVoList(websiteHits,DirectoryTypeEnum.WEBSITE.getType()));
        return ResultUtils.success(directoryVoList);
    }

    public BaseResponse<List<DirectoryPositionResp>> getPositionByPid(Long pid){
        List<DirectoryPositionResp> directoryPosition = new ArrayList<>();
        getPositionByPid(pid, new Callback() {
            @Override
            public void onCallback(Category category) {
                String name = category.getName();
                if(StringUtils.isNotBlank(name)){
                    DirectoryPositionResp directoryPositionResp = new DirectoryPositionResp();
                    directoryPositionResp.setLabel(name);
                    directoryPositionResp.setId(category.getId());
                    directoryPosition.add(directoryPositionResp);
                }
                if(category.getPid().equals(0L)){
                    DirectoryPositionResp directoryPositionResp = new DirectoryPositionResp();
                    directoryPositionResp.setLabel("全部文件");
                    directoryPositionResp.setId(0L);
                    directoryPosition.add(directoryPositionResp);
                }
            }
        });
        Collections.reverse(directoryPosition);
        return ResultUtils.success(directoryPosition);
    }
    public BaseResponse<List<CommonDirectory>> getDirectory(Long pid, HttpServletRequest httpServletRequest){
        Long userId = ManageUserInfo.getUser().getId();
        List<Website> websites = websiteService.getAllByPid(pid,userId);
        List<Category> categories = categoryService.getCategoryDao().getByPid(userId, pid);
        List<UploadFile> uploadFileList = uploadFileService.getUploadFileDao().getFileByPidUserId(userId, pid);
        List<CommonDirectory> totalData = new ArrayList<>();
        websites.stream().forEach(site->{
            CommonDirectory directoryVo = new CommonDirectory();
            DirectoryAdapter.setWebsiteResp(site,directoryVo);
            totalData.add(directoryVo);
        });
        categories.stream().forEach(category->{
            CommonDirectory directoryVo = new CommonDirectory();
            DirectoryAdapter.setCategoryResp(category,directoryVo);
            totalData.add(directoryVo);
        });
        uploadFileList.stream().forEach(uploadFile->{
            CommonDirectory directoryVo = new CommonDirectory();
            DirectoryAdapter.setUploadFileResp(uploadFile,directoryVo);
            totalData.add(directoryVo);
        });

        List<CommonDirectory> collect = totalData.stream().sorted(
                Comparator.comparing(CommonDirectory::getUpdateTime).reversed()).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }
    public List<Category> getCategories(List<CommonDirectory> directoryVos){
        return directoryVos.stream().filter(directoryVo -> directoryVo.getType() == DirectoryTypeEnum.CATEGORY.getType()).map(value -> {
            Category category = new Category();
            BeanUtil.copyProperties(value, category);
            return category;
        }).collect(Collectors.toList());
    }
    public List<Website> getWebsites(List<CommonDirectory> directoryVos){
        return directoryVos.stream().filter((directoryVo -> directoryVo.getType() == DirectoryTypeEnum.WEBSITE.getType())).map(value -> {
            Website website = new Website();
            BeanUtil.copyProperties(value, website);
            return website;
        }).collect(Collectors.toList());
    }
    public BaseResponse<String> deleteDirectory(@RequestBody List<CommonDirectory> directoryVos){
        List<Long> categories  = directoryVos.stream().filter((directoryVo -> directoryVo.getType() == DirectoryTypeEnum.CATEGORY.getType())).map(CommonDirectory::getId).collect(Collectors.toList());
        List<Long> websites  = directoryVos.stream().filter((directoryVo -> directoryVo.getType() == DirectoryTypeEnum.WEBSITE.getType())).map(CommonDirectory::getId).collect(Collectors.toList());
        List<Long> uploadFileList  = directoryVos.stream().filter((directoryVo -> directoryVo.getType() == DirectoryTypeEnum.UPLOAD_FILE.getType())).map(CommonDirectory::getId).collect(Collectors.toList());
        websiteService.deleteWebsite(websites);
        categoryService.deleteCategory(categories);
        uploadFileService.logicDeleteUploadFile(uploadFileList);
        applicationEventPublisher.publishEvent(new DeleteDataEvent(this,directoryVos));
        return ResultUtils.success("ok");
    }

    public List<UploadFile> getUploadFileList(List<CommonDirectory> directories){
        return directories.stream().filter((directoryVo -> directoryVo.getType() == DirectoryTypeEnum.UPLOAD_FILE.getType())).map(value -> {
            UploadFile uploadFile = new UploadFile();
            BeanUtil.copyProperties(value, uploadFile);
            return uploadFile;
        }).collect(Collectors.toList());
    }
    public BaseResponse<String> cutDirectory(@RequestBody @Valid PasteToDiectoryReq pasteToDiectoryReq){ //剪贴目录
        List<CommonDirectory> directoryVos = pasteToDiectoryReq.getDirectoryVos();
        Long target = pasteToDiectoryReq.getPid();
        List<Category> categories = getCategories(directoryVos);
        List<Website> websites = getWebsites(directoryVos);
        List<UploadFile> uploadFileList = getUploadFileList(directoryVos);
        Long userId = ManageUserInfo.getUser().getId();
        Date date = new Date();
        websiteService.cutWebsite(websites,date,target);
        uploadFileService.cutUploadFile(uploadFileList,date,target);
        //将该目录下的所有分类和网址都指向要剪贴的目录，子目录不需要关心，因为子目录通过pid字段指向该目录
        String errorResult = categoryService.cutCategory(categories, date, userId, target);
        AssertUtil.isEmpty(errorResult,errorResult);
        return ResultUtils.success("ok");
    }
    //使用旧的id列表通过pid字段查询之前的记录，记录新的id列表设置pid字段，将之前的记录更改后新增完成当前目录复制选中的目录
    public void copyDirectoryChildren(List<Long> oldPids,List<Long> newPids,Long userId,Date date){
        if(CollectionUtil.isEmpty(oldPids)) return;
        for(int i = 0; i < oldPids.size(); i++){
            Long pid = oldPids.get(i);
            List<Category> categories = categoryService.getCategoryDao().getByPid(userId, pid);
            List<Website> websites = websiteService.getAllByPid(pid,userId);
            List<UploadFile> uploadFileList = uploadFileService.getUploadFileDao().getFileByPidUserId(userId, pid);
            //拷贝旧分类
            List<Long> nextOldPids = BeanUtil.copyToList(categories,Category.class).stream().map(Category::getId).collect(Collectors.toList());
            Long newPid = newPids.get(i);
            categories.forEach(value->{
                value.setPid(newPid);value.setId(null);
                value.setCreateTime(date); value.setUpdateTime(date);
                value.setIsDelete(LogicDeleteTypeEnum.NO_DELETE.getType());
                categoryService.getCategoryDao().getBaseMapper().insert(value);
            });
            websites.forEach(value->{
                value.setPid(newPid);value.setId(null);
                value.setCreateTime(date); value.setUpdateTime(date);
                value.setIsDelete(LogicDeleteTypeEnum.NO_DELETE.getType());
                websiteService.getWebsiteDao().getBaseMapper().insert(value);
            });
            uploadFileList.forEach(value->{
                value.setPid(newPid);value.setId(null);
                value.setCreateTime(date); value.setUpdateTime(date);
                value.setIsDelete(LogicDeleteTypeEnum.NO_DELETE.getType());
                uploadFileService.getUploadFileDao().getBaseMapper().insert(value);
            });

            List<Long> collect = categories.stream().map(Category::getId).collect(Collectors.toList());
            copyDirectoryChildren(nextOldPids,collect,userId,date);
        }
    }
    public BaseResponse<String> copyDirectory(@RequestBody PasteToDiectoryReq pasteToDiectoryReq){  //复制目录
        List<CommonDirectory> directoryVos = pasteToDiectoryReq.getDirectoryVos();
        Long pid = pasteToDiectoryReq.getPid();
        List<Category> categories = getCategories(directoryVos);
        //拷贝旧记录进行递归查询
        List<Category> oldCategories = BeanUtil.copyToList(categories,Category.class);
        List<Website> websites = getWebsites(directoryVos);
        List<UploadFile> uploadFileList = getUploadFileList(directoryVos);
        Date date = new Date();
        Long userId = ManageUserInfo.getUser().getId();
        //复制一级目录
        categoryService.copyCategory(categories,userId,pid,date);
        websiteService.copyWebsites(websites,userId,pid,date);
        uploadFileService.copyUploadList(uploadFileList,userId,pid,date);
        //递归子目录
        copyDirectoryChildren(oldCategories.stream().map(Category::getId).collect(Collectors.toList()),
                categories.stream().map(Category::getId).collect(Collectors.toList()),userId, date);
        return ResultUtils.success("ok");
    }
}
