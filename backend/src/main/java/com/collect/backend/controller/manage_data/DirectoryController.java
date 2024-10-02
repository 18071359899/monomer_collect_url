package com.collect.backend.controller.manage_data;

import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.dao.UserUseTotalDao;
import com.collect.backend.domain.entity.User;
import com.collect.backend.domain.entity.UserUseTotal;
import com.collect.backend.domain.vo.cache.UserSpaceCache;
import com.collect.backend.domain.vo.req.PasteToDiectoryReq;
import com.collect.backend.domain.vo.resp.directory.CommonDirectory;
import com.collect.backend.domain.vo.resp.directory.DetailsDirectoryVo;
import com.collect.backend.domain.vo.resp.directory.DirectoryPositionResp;
import com.collect.backend.service.impl.manage_data.DirectoryServiceImpl;
import com.collect.backend.utils.ByteConverter;
import com.collect.backend.utils.ManageUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


/**
 * 对某个目录下的文件夹和文件执行增删改查
 */
@RestController
@RequestMapping("/directory")
public class DirectoryController {
    @Autowired
    private DirectoryServiceImpl directoryService;
    @Autowired
    private UserUseTotalDao userUseTotalDao;
    /**
     * 获取用户上传文件的使用空间和总空间
     */
    @GetMapping("/get/user/space")
    public BaseResponse<UserSpaceCache> getUserSpace(){
        UserUseTotal userUseTotalByUserId = userUseTotalDao.getUserUseTotalByUserId(ManageUserInfo.getUser().getId());
        UserSpaceCache userSpaceCache = new UserSpaceCache();
        userSpaceCache.setUserFileUseSpace(ByteConverter.convertBytesToReadableSize(userUseTotalByUserId.getUserUse()));
        userSpaceCache.setUserFileTotalSpace(ByteConverter.convertBytesToReadableSize(userUseTotalByUserId.getUserTotal()));
        userSpaceCache.setUserFileTotalSpaceValue(Double.valueOf(userUseTotalByUserId.getUserTotal()));
        userSpaceCache.setUserFileUseSpaceValue(Double.valueOf(userUseTotalByUserId.getUserUse()));
        return ResultUtils.success(userSpaceCache);
    }
    /**
     * 某个目录下的某个文件夹或文件的详细信息
     * @param id
     * @param type
     * @return
     */
    @GetMapping("/details/{type}/{id}")
    public BaseResponse<DetailsDirectoryVo> detailsDirectoryVoBaseResponse(@PathVariable("id") Long id, @PathVariable("type") Integer type){
        return directoryService.detailsDirectoryVoBaseResponse(id,type);
    }
    @GetMapping("/search/")
    public BaseResponse<List<CommonDirectory>> searchDirectory(String search){
        return directoryService.searchDirectory(search);
    }
    @GetMapping("/get/position/")
    public BaseResponse<List<DirectoryPositionResp>> getPositionByPid(Long pid){
        return directoryService.getPositionByPid(pid);
    }
    @GetMapping("/get/")
    public BaseResponse<List<CommonDirectory>> getDirectory(Long pid, HttpServletRequest httpServletRequest){
        return directoryService.getDirectory(pid,httpServletRequest);
    }
    @PostMapping("/delete/")
    public BaseResponse<String> deleteDirectory(@RequestBody List<CommonDirectory> directoryVos){
        return directoryService.deleteDirectory(directoryVos);
    }
    @PostMapping("/cut/")
    public BaseResponse<String> cutDirectory(@RequestBody @Valid PasteToDiectoryReq pasteToDiectoryReq){ //剪贴目录
        return directoryService.cutDirectory(pasteToDiectoryReq);
    }


    @PostMapping("/copy/")
    public BaseResponse<String> copyDirectory(@RequestBody PasteToDiectoryReq pasteToDiectoryReq){  //复制目录
        return directoryService.copyDirectory(pasteToDiectoryReq);
    }
}
