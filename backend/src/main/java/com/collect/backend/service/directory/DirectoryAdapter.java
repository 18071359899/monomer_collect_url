package com.collect.backend.service.directory;

import cn.hutool.core.bean.BeanUtil;
import com.collect.backend.common.enums.DirectoryTypeEnum;
import com.collect.backend.common.enums.FileTypeEnum;
import com.collect.backend.domain.entity.Category;
import com.collect.backend.domain.entity.UploadFile;
import com.collect.backend.domain.entity.Website;
import com.collect.backend.domain.vo.resp.directory.CommonDirectory;
import com.collect.backend.utils.file.FileNameSameResetName;
import com.collect.backend.utils.fileType.MimeTypeUtils;

import java.util.List;

public class DirectoryAdapter {
    public static void setWebsiteResp(Website website, CommonDirectory commonDirectory){
        BeanUtil.copyProperties(website,commonDirectory);
        commonDirectory.setType(DirectoryTypeEnum.WEBSITE.getType());
    }
    public static void setCategoryResp(Category category,CommonDirectory commonDirectory){
        BeanUtil.copyProperties(category,commonDirectory);
        commonDirectory.setType(DirectoryTypeEnum.CATEGORY.getType());
    }
    public static void setUploadFileResp(UploadFile uploadFile, CommonDirectory commonDirectory){
        BeanUtil.copyProperties(uploadFile,commonDirectory);
        commonDirectory.setName(uploadFile.getFileName());
        String path = uploadFile.getPath();
        commonDirectory.setFilePath(path);
        String suffix = MimeTypeUtils.getSuffixNoPoint(path);
        commonDirectory.setIcon(uploadFile.getThumbnail());
        FileTypeEnum fileTypeByFileSuffix = MimeTypeUtils.getFileTypeByFileSuffix(suffix);
        if(fileTypeByFileSuffix.getType().equals(FileTypeEnum.MOVIE_TYPE.getType())){  //加上m3u8文件路径
            path = path.substring(0,path.lastIndexOf("."));
            String readlFileName = path.substring(path.lastIndexOf("/") + 1);
            path = path  + "/" +readlFileName + ".m3u8";
            commonDirectory.setFilePath(path);
            commonDirectory.setVideoFilePath(uploadFile.getPath());
        }
        commonDirectory.setType(DirectoryTypeEnum.UPLOAD_FILE.getType());
    }

    //查找文件名称是否相同
    public static String searchFileNameIsRepeat(String name, List<UploadFile> uploadFileList){
        //搜寻该目录下所有文件夹名称,不能重复
        for(UploadFile uploadFile: uploadFileList){
            if(uploadFile.getFileName().equals(name)){
                return FileNameSameResetName.resetNewNoRepeated(name);
            }
        }
        return null;
    }

    //查找分类名称是否相同
    public static String searchCategoryNameIsRepeat(String name,List<Category> categories){
        //搜寻该目录下所有文件夹名称,不能重复
        for(Category ct: categories){
            if(ct.getName().equals(name)){
                return FileNameSameResetName.resetNewNoRepeated(name);
            }
        }
        return null;
    }
}
