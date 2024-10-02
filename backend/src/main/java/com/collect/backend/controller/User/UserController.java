package com.collect.backend.controller.User;

import com.collect.backend.common.BaseResponse;
import com.collect.backend.domain.vo.resp.UserVo;
import com.collect.backend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/account")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register/")
    public Map<String,String> register(@RequestParam Map<String,String> map){
        return userService.register(map.get("username"),map.get("password"),map.get("confirmPassword"));
    }
    @GetMapping("/info/")
    public Map<String,String> getInfo(){
        return userService.getInfo();
    }

    /**
     * 获取某个用户的个人中心页面
     * @return
     */
    @GetMapping("/get/homepage/")
    public BaseResponse<UserVo> getHomePage(Long id){
        return userService.getHomePage(id);
    }
    @PostMapping("/token/")
    //RequestParam注解是将post请求中的参数拿出来用
    public Map<String,String> getToken(@RequestParam  Map<String,String> map){
        return userService.getToken(map.get("username"),map.get("password"),map.get("code"),map.get("uuid"));
    }
}
