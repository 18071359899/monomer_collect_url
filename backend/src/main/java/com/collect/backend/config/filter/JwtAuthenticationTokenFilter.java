package com.collect.backend.config.filter;

import com.collect.backend.common.exception.BusinessException;
import com.collect.backend.domain.entity.User;
import com.collect.backend.mapper.UserMapper;
import com.collect.backend.service.impl.utils.UserDetailsImpl;
import com.collect.backend.utils.jwt.JwtUtil;
import com.collect.backend.utils.redis.RedisUtils;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);
        String userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //从redis中获取登陆后的JWT
        String redisJWT =  RedisUtils.getStr(userid.toString());
        if(!token.equals(redisJWT)){  //不相等代表当前登陆的用户已经在其他端登陆了
            filterChain.doFilter(request, response);
            return;
        }


        User user = userMapper.selectById(Long.parseLong(userid));

        if (user == null) {
            throw  new BusinessException("用户未登录");
        }

        UserDetailsImpl loginUser = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, null);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
