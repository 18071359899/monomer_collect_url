package com.collect.backend.service.impl.manage_data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.common.enums.LogicDeleteTypeEnum;
import com.collect.backend.common.exception.CommonErrorEnum;
import com.collect.backend.dao.WebsiteDao;
import com.collect.backend.domain.entity.Website;
import com.collect.backend.domain.vo.resp.directory.CommonDirectory;
import com.collect.backend.service.adpter.WebsiteAdpter;
import com.collect.backend.service.directory.DirectoryAdapter;
import com.collect.backend.service.manage_url.WebsiteService;
import com.collect.backend.utils.assertBussion.AssertUtil;
import com.collect.backend.utils.ManageUserInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

/**
* @author Lenovo
* @description 针对表【website】的数据库操作Service实现
* @createDate 2024-02-06 21:10:39
*/
@Service
@Slf4j
@Data
public class WebsiteServiceImpl implements WebsiteService{
    @Autowired
    private WebsiteDao websiteDao;
    @Autowired
    private WebsiteAdpter websiteAdpter;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public List<Website> getAllByPid(Long pid, Long userId) {
        return websiteDao.getAllByPid(pid,userId);
    }
    @Override
    public BaseResponse<String> addWebsite(Website website) {
        Map<String, String> result = websiteAdpter.parseUrlGetIconTitle(website);
        if(StringUtils.isNotBlank(result.get("message"))){
            return ResultUtils.error(CommonErrorEnum.PARAM_INVALID.getCode(),result.get("message"));
        }
        Date date = new Date();
        Website newWebsite = new Website(null, website.getUrl(), result.get("title"),
                website.getDescribe(), result.get("icon"), website.getPid()
                , 0, date, date, ManageUserInfo.getUser().getId());
        int insert = websiteDao.getBaseMapper().insert(newWebsite);
        if(insert > 0){
            return ResultUtils.success("ok");
        }
        return ResultUtils.error(CommonErrorEnum.PARAM_INVALID);
    }

    @Override
    public BaseResponse<String> updateWebsite(Website website) {
        if(Objects.isNull(website.getId())){
            return ResultUtils.error(CommonErrorEnum.PARAM_INVALID.getCode(),"id不能为空");
        }
        Map<String, String> reslt = websiteAdpter.parseUrlGetIconTitle(website);
        if(StringUtils.isNotBlank(reslt.get("message"))){
            return ResultUtils.error(CommonErrorEnum.PARAM_INVALID.getCode(),reslt.get("message"));
        }
        Date date = new Date();
        Website updateWebsite = new Website(website.getId(), website.getUrl(), reslt.get("title"),
                website.getDescribe(), reslt.get("icon"), website.getPid()
                , 0, null, date, ManageUserInfo.getUser().getId());
        int update = websiteDao.getBaseMapper().updateById(updateWebsite);
        if(update > 0){
            return ResultUtils.success("ok");
        }
        return ResultUtils.error(CommonErrorEnum.PARAM_INVALID);
    }

    @Override
    public BaseResponse<Website> getWebsite(Long id) {
        AssertUtil.isNotEmpty(id,"id不能为空");
        QueryWrapper<Website> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        queryWrapper.eq("is_delete",LogicDeleteTypeEnum.NO_DELETE.getType());
        Website website = websiteDao.getBaseMapper().selectOne(queryWrapper);
        AssertUtil.isNotEmpty(website,"网址不存在");
        return ResultUtils.success(website);
    }

    @Override
    public BaseResponse<String> deleteWebsite(List<Long> ids) {
        for(Long id: ids){
            websiteDao.logicDelete(id);
        }
        return ResultUtils.success("ok");
    }

    @Override
    public void copyWebsites(List<Website> websites,Long userId,Long pid,Date date) {
        websites.forEach(value->{
            value.setPid(pid);value.setId(null);value.setUserId(userId);
            value.setCreateTime(date); value.setUpdateTime(date);
            value.setIsDelete(LogicDeleteTypeEnum.NO_DELETE.getType());
            websiteDao.getBaseMapper().insert(value);
        });
    }

    @Override
    public BaseResponse<List<CommonDirectory>> listWebsite() {
        Long userId = ManageUserInfo.getUser().getId();
        List<Website> websiteList = websiteDao.getAllByUserId(userId);
        List<CommonDirectory> commonDirectoryList = new ArrayList<>();
        for (Website website : websiteList) {
            CommonDirectory commonDirectory = new CommonDirectory();
            DirectoryAdapter.setWebsiteResp(website,commonDirectory);
            commonDirectoryList.add(commonDirectory);
        }
        return ResultUtils.success(commonDirectoryList);
    }

    public void cutWebsite(List<Website> websites,Date date, Long pid) {
        websites.forEach(value->{
            value.setPid(pid);
            value.setUpdateTime(date);
            websiteDao.getBaseMapper().updateById(value);
        });
    }
}




