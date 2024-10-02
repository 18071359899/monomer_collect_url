package com.collect.backend.dao;

import com.collect.backend.domain.entity.UserUseTotal;
import com.collect.backend.mapper.UserUseTotalMapper;
import com.collect.backend.utils.ManageUserInfo;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class UserUseTotalDao  extends ServiceImpl<UserUseTotalMapper, UserUseTotal>{
    /**
     * 更新用户上传文件空间
     * @param fileSize
     * @param type
     * @return
     */
    public boolean updateUserUploadFileSpace(Long fileSize,Integer type) {  //type：0：添加  1删除
        Long userId = ManageUserInfo.getUser().getId();
        UserUseTotal userUseTotal = getUserUseTotalByUserId(userId);
        Long newUseSpace = 0L;
        if(type.equals(0)){
            newUseSpace =   userUseTotal.getUserUse() + fileSize;
        }else{
            newUseSpace =   userUseTotal.getUserUse() - fileSize;
        }
        return lambdaUpdate().eq(UserUseTotal::getUserUse,userId)
                .set(UserUseTotal::getUserUse,newUseSpace).
                update();
    }

    public UserUseTotal getUserUseTotalByUserId(Long userId){
        return lambdaQuery().eq(UserUseTotal::getUserId, userId).one();
    }
}
