package com.collect.backend.service.impl.user;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.Constants;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.dao.UserUseTotalDao;
import com.collect.backend.domain.entity.User;
import com.collect.backend.domain.entity.UserUseTotal;
import com.collect.backend.domain.vo.resp.UserVo;
import com.collect.backend.mapper.UserMapper;
import com.collect.backend.service.adpter.UserAdpter;
import com.collect.backend.service.impl.utils.UserDetailsImpl;
import com.collect.backend.service.user.UserService;
import com.collect.backend.utils.jwt.JwtUtil;
import com.collect.backend.utils.ManageUserInfo;
import com.collect.backend.utils.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserUseTotalDao userUseTotalDao;
    @Autowired
    private UserAdpter userAdpter;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Map<String, String> register(String username, String password, String confirmPassword) {
        Map<String,String> map = new HashMap<>();
        if(username == null){
            map.put("error_message","用户名不能为空");
            return map;
        }
        if(password == null || confirmPassword == null){
            map.put("error_message","密码不能为空");
            return map;
        }

        username = username.trim(); //清空空格，特殊字符等
        if(username.length() == 0){
            map.put("error_message","用户名不能为空");
            return map;
        }

        if(password.length() == 0 || confirmPassword.length() == 0){
            map.put("error_message","密码不能为空");
            return map;
        }

        if(!password.equals(confirmPassword)){
            map.put("error_message","两次密码不一样");
            return map;
        }

        if(username.length() > 100){
            map.put("error_message","用户名长度不能大于100");
            return map;
        }

        if(password.length() > 100 || confirmPassword.length() > 100){
            map.put("error_message","密码长度不能大于100");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        List<User> users = userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            map.put("error_message","用户名已经存在");
            return map;
        }

        String encodePassword = passwordEncoder.encode(password);
        String photo = "https://img0.baidu.com/it/u=3509076742,1241817598&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=703";

        Date date = new Date();
        User user = new User(null,username,encodePassword,photo,0,date
                ,0,0,"",date);
        int insert = userMapper.insert(user);
        userUseTotalDao.getBaseMapper().insert(
                new UserUseTotal(null,user.getId(),0L,1024*1024*10L)
        );
        map.put("error_message","successfully");
        return map;
    }

    @Override
    public BaseResponse<UserVo> getHomePage(Long id) {
        User user = userMapper.selectById(id);
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user,userVo);
        //查询当前用户是否关注了他
        Long loginId = ManageUserInfo.getUser().getId();
        userAdpter.setUserInfo(userVo,loginId);
        return ResultUtils.success(userVo);
    }

    @Override
    public Map<String, String> getToken(String username, String password,String code,String uuid) {
       /*这几行代码执行的意思是进行用户名和密码的身份验证。首先，用户名和密码被封装到一个`UsernamePasswordAuthenticationToken`对象中。然后，
       使用`authenticationManager`进行身份验证，如果验证失败，则会自动处理。如果验证成功，则会返回一个已验证的`Authentication`对象。
       通过`authenticate.getPrincipal()`方法获取到`UserDetailsImpl`对象，并通过该对象获取到`User`对象，即当前登录的用户。*/
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken  =
                new UsernamePasswordAuthenticationToken(username,password);

        Map<String,String> map = new HashMap<>();
        //校验验证码
        String redisCode = (String) RedisUtils.getStr(Constants.codeKey + uuid);
        if(Objects.isNull(redisCode) || !code.equals(redisCode)){
            map.put("error_message","验证码错误");
            return map;
        }

        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken); //登录失败，自动处理
        //解析成我们要用的对象user
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticate.getPrincipal();
        User user = userDetails.getUser();




        //生成jwt
        String jwt = JwtUtil.createJWT(user.getId().toString());
        //如果当前已经登陆过用户，那就设置新的jwt，之前的自然也就登陆不了了
        //走redis
        RedisUtils.set(user.getId().toString(),jwt); //jwt存储到redis中
        RedisUtils.expire(user.getId().toString(),60 * 60 * 1000L * 24 * 14);


        map.put("error_message","successfully");
        map.put("token",jwt);
        return map;
    }

    @Override
    public Map<String, String> getInfo() {
        /*这几行代码执行的意思是获取当前已经通过身份验证的用户信息。首先，通过`SecurityContextHolder.getContext().getAuthentication()`
        方法获取到当前的身份验证对象，这里是`UsernamePasswordAuthenticationToken`类型。然后，从`UsernamePasswordAuthenticationToken
        对象中获取到`UserDetailsImpl`对象，即已验证的用户信息。最后，通过`UserDetailsImpl`对象获取到`User`对象，即当前登录的用户。*/
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userDetails = (UserDetailsImpl)  usernamePasswordAuthenticationToken.getPrincipal();
        User user = userDetails.getUser();

        Map<String,String> map = new HashMap<>();
        map.put("error_message","successfully");
        map.put("id",user.getId().toString());
        map.put("username",user.getUsername());
        map.put("photo",user.getPhoto());
        return map;
    }
}
