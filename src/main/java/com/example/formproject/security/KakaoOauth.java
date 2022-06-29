package com.example.formproject.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Getter
public class KakaoOauth {
    @Value("${spring.security.oauth2.client.provider.kakao.token_uri}")
    private String KAKAO_TOKEN_URL;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String KAKAO_USER_INFO_URL;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;
    private String REDIRECT_URI = "http://localhost:3000/code/auth";

}
