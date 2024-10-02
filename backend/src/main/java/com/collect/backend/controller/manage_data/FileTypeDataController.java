package com.collect.backend.controller.manage_data;

import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.enums.FileTypeEnum;
import com.collect.backend.dao.UploadFileDao;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.domain.vo.resp.directory.CommonDirectory;
import com.collect.backend.service.impl.manage_data.FileTypeDataServiceImpl;
import com.collect.backend.utils.ManageUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 上传文件的数据分类获取接口
 */
@RestController
@RequestMapping("/fileType")
public class FileTypeDataController {
    @Autowired
    private FileTypeDataServiceImpl fileTypeDataService;
    /**
     * 获取当前用户某个文件类型对应的所有数据
     */
    @GetMapping("/get/")
    public BaseResponse<List<CommonDirectory>> getFileTypeData(Integer type){
        return fileTypeDataService.getVideoTypeData(type);
    }

}
