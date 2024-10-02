package com.collect.backend.mapper;

import com.collect.backend.domain.entity.UploadFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【file(文件上传表)】的数据库操作Mapper
* @createDate 2024-04-18 00:20:43
* @Entity com.collect.backend.domain.entity.File
*/
@Mapper
public interface FileMapper extends BaseMapper<UploadFile> {

}




