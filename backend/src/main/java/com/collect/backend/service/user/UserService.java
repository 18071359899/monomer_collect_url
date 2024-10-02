package com.collect.backend.service.user;

import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.vo.resp.UserVo;

import java.util.Map;

public interface UserService {
    public Map<String,String> getInfo();
    public Map<String,String> getToken(String username,String password,String code,String uuid);
    public Map<String,String> register(String username
            ,String password,String confirmPassword);

    BaseResponse<UserVo> getHomePage(Long id);
}
