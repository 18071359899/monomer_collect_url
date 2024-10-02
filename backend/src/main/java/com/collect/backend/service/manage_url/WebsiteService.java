package com.collect.backend.service.manage_url;

import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.Website;
import com.baomidou.mybatisplus.extension.service.IService;
import com.collect.backend.domain.vo.req.DeleteListByIds;
import com.collect.backend.domain.vo.resp.directory.CommonDirectory;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
* @author Lenovo
* @description 针对表【website】的数据库操作Service
* @createDate 2024-02-06 21:10:39
*/
public interface WebsiteService {
    List<Website> getAllByPid(Long pid,Long userId);
    BaseResponse<String> addWebsite(Website website);
    BaseResponse<String> updateWebsite(Website website);
    BaseResponse<Website> getWebsite(Long id);
    BaseResponse<String> deleteWebsite(List<Long> ids);

    void copyWebsites(List<Website> websites,Long userId,Long pid, Date date);

    BaseResponse<List<CommonDirectory>> listWebsite();
}
