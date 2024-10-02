package com.collect.backend.service.impl.user_use_file_space;

import com.collect.backend.common.Constants;
import com.collect.backend.dao.UserUseTotalDao;
import com.collect.backend.utils.ManageUserInfo;
import com.collect.backend.utils.redis.RedisUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
public class UserUseTotalAdapter {
    @Autowired
    private UserUseTotalDao userUseTotalDao;
    public void updateUserUploadFileSpace(Long fileSize,Integer type){
        userUseTotalDao.updateUserUploadFileSpace(fileSize,type);
        Long userId = ManageUserInfo.getUser().getId();
        RedisUtils.del(Constants.getUserUseFileKey(userId));
    }
}
