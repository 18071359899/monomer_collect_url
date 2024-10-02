package com.collect.backend.utils;

import com.collect.backend.domain.entity.User;
import com.collect.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class ManageUserInfo {
    public static User getUser(){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl)  usernamePasswordAuthenticationToken.getPrincipal();
        User user = userDetails.getUser();
        return user;
    }
}
