package com.example.formproject.security;

import com.example.formproject.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
        String reToken = request.getHeader("RefreshToken");
//        if(tokenCookie != null) {
//            token = tokenCookie.getValue();
//        }else {
//
//        }
        // 유효한 토큰인지 확인
        if (token != null && reToken != null) {
            String jwtToken = token.replaceAll("Bearer ", "");
            String refreshToken = reToken.replace("Bearer ", "");
            if (provider.validateToken(refreshToken) && provider.checkRefreshToken(refreshToken,jwtToken)){

                if (provider.validateToken(jwtToken)) {
                    // 토큰값과 refresh 토큰으로 유저 정보를 받아옴
                    MemberDetail detail = provider.getMemberDetail(jwtToken);
                    if (detail.getMember() != null) {
                        Authentication authentication = provider.getAuthentication(detail);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } else {
                    String pk = provider.getUserPk(jwtToken);
                    Member m = provider.getMember(Integer.parseInt(pk));
                    String newToken = provider.generateToken(m);
                    provider.saveRefreshToken(refreshToken,newToken);
                    provider.setAuthHeader(response, newToken);
                    MemberDetail detail = new MemberDetail(m);
                    Authentication authentication = provider.getAuthentication(detail);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    provider.setAuthHeader(response,newToken);
                }
            }


        }
        chain.doFilter(request, response);
    }
}
