package com.example.formproject.service;

import com.example.formproject.configuration.RestConfig;
import com.example.formproject.dto.response.JwtResponseDto;
import com.example.formproject.entity.Member;
import com.example.formproject.exception.AuthenticationException;
import com.example.formproject.repository.MemberRepository;
import com.example.formproject.security.JwtProvider;
import com.example.formproject.security.KakaoOauth;
import com.example.formproject.security.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final KakaoOauth kakaoOauth;
    private final JwtProvider provider;
    private final MemberRepository memberRepository;
    private final RestTemplate template;

    public String getAccessToken(String code){

        MultiValueMap<String,Object> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id",kakaoOauth.getKAKAO_CLIENT_ID());
        params.add("redirect_uri",kakaoOauth.getREDIRECT_URI());
        params.add("code",code);
        ResponseEntity<Map> response = template.postForEntity(kakaoOauth.getKAKAO_TOKEN_URL(),params,Map.class);
        if(response.getStatusCode() != HttpStatus.OK)
            throw new IllegalArgumentException("코드가 잘못되었습니다.");
        Map<String,Object> ret = response.getBody();
        return ret.get("access_token").toString();
    }
    @Transactional
    public JwtResponseDto kakaoLogin(String code, HttpServletResponse response) throws AuthenticationException {
        String accessToken = getAccessToken(code);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer "+accessToken);
        HttpEntity request = new HttpEntity(headers);


        ResponseEntity<Map> res = template.exchange(kakaoOauth.getKAKAO_USER_INFO_URL(),HttpMethod.GET,request,Map.class);
        OAuthAttributes attr = OAuthAttributes.ofKakao(null,res.getBody());
        Member m = memberRepository.findByEmail(attr.getEmail()).orElse(null);
        if(m == null){
            m = Member.builder()
                    .nickname(attr.getName())
                    .email(attr.getEmail())
                    .build();
        }
        m.updateMember(attr);
        memberRepository.save(m);
        return provider.generateToken(m,response);
    }
}