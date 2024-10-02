package com.collect.backend.controller.manage_data;


import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.entity.Website;
import com.collect.backend.domain.vo.req.DeleteListByIds;
import com.collect.backend.domain.vo.resp.directory.CommonDirectory;
import com.collect.backend.service.manage_url.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/website")
public class WebsiteController {
    @Autowired
    private WebsiteService websiteService;

    /**
     * 获取当前用户下存储的所有网址
     * @return
     */
    @GetMapping("/list/")
    public BaseResponse<List<CommonDirectory>> listWebsite(){
        return websiteService.listWebsite();
    }

    @PostMapping("/add/")
    public BaseResponse<String> addWebsite(@Valid @RequestBody Website website){
        return websiteService.addWebsite(website);
    }

    @PostMapping("/update/")
    public BaseResponse<String> updateWebsite(@Valid  @RequestBody Website website){
        return websiteService.updateWebsite(website);
    }

    @GetMapping("/get/{id}")
    public BaseResponse<Website> getWebsite(@PathVariable Long id){
        return websiteService.getWebsite(id);
    }
    @GetMapping("/delete/")
    public BaseResponse<String> getWebsite(@Valid @RequestBody DeleteListByIds ids){
        return websiteService.deleteWebsite(ids.getIds());
    }
}
