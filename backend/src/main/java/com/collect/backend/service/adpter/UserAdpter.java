package com.collect.backend.service.adpter;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.collect.backend.dao.UserDao;
import com.collect.backend.dao.UserFollowDao;
import com.collect.backend.domain.entity.UserFollow;
import com.collect.backend.domain.vo.resp.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserAdpter {

    @Autowired
    private UserFollowDao userFollowDao;
    public void setUserInfo(UserVo userVo,Long loginId) {
        Long id = userVo.getId();
        if(!loginId.equals(id)){
            UserFollow userFollow = userFollowDao.queryByUserIdAndFollowUserId(loginId, id);
            if(Objects.nonNull(userFollow)){
                userVo.setIsFollow(1);
            }else{
                userVo.setIsFollow(0);
            }
        }
        if(StringUtils.isBlank(userVo.getIntroduction())){
            userVo.setIntroduction("暂无个人简介");
        }
    }
}
