package com.collect.backend.service.impl.manage_data;

import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.dao.UploadFileDao;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.domain.vo.resp.directory.CommonDirectory;
import com.collect.backend.service.directory.DirectoryAdapter;
import com.collect.backend.utils.ManageUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileTypeDataServiceImpl {
    @Autowired
    private UploadFileDao uploadFileDao;
    public List<CommonDirectory> getCommonDirectoryListByUploadFileList(List<UploadFile> uploadFileList){
        List<CommonDirectory> commonDirectoryList = new ArrayList<>();
        for (UploadFile uploadFile : uploadFileList) {
            CommonDirectory commonDirectory = new CommonDirectory();
            DirectoryAdapter.setUploadFileResp(uploadFile,commonDirectory);
            commonDirectoryList.add(commonDirectory);
        }
        return commonDirectoryList;
    }
    public BaseResponse<List<CommonDirectory>> getVideoTypeData(Integer type) {
        Long userId = ManageUserInfo.getUser().getId();
        List<UploadFile> uploadFileList = uploadFileDao.getFileByUserIdType(userId,type);
        return ResultUtils.success(getCommonDirectoryListByUploadFileList(uploadFileList));
    }
}
