package com.example.formproject.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private JwtProvider provider;

    @Autowired
    public JwtAuthFilter(JwtProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
//        Cookie tokenCookie = CookieUtils.getCookie(request,"token").orElse(null);
        String token  = request.getHeader("Authorization");
//        if(tokenCookie != null) {
//            token = tokenCookie.getValue();
//        }else {
//
//        }
        // 유효한 토큰인지 확인
        if (token != null) {
            String jwtToken = token.replaceAll("Bearer ", "");
            if (provider.validateToken(jwtToken)) {
                // 토큰값과 refresh 토큰으로 유저 정보를 받아옴
                MemberDetail detail = provider.getMemberDetail(jwtToken);
                if (detail.getMember() != null) {
                    Authentication authentication = provider.getAuthentication(detail);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
