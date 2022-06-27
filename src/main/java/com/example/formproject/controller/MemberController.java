package com.example.formproject.controller;

import com.example.formproject.dto.request.LoginDto;
import com.example.formproject.dto.request.MailDto;
import com.example.formproject.dto.request.MemberRequestDto;
import com.example.formproject.exception.AuthenticationException;
import com.example.formproject.service.EmailService;
import com.example.formproject.service.MemberService;
import com.nimbusds.jose.util.IOUtils;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService;

    @PostMapping("/member/login")
    public String loginMember(@RequestBody  LoginDto dto, HttpServletResponse response) throws AuthenticationException {
        String token = memberService.login(dto);

        response.addHeader("Authorization","Bearer "+token);
        return "Bearer "+token;
    }
    @PostMapping("/member/email")
    public String emailToken(MailDto dto) throws MessagingException {
        String randomChars = RandomString.make(6);
        return emailService.sendHtmlEmail(dto,randomChars);
    }
    @PostMapping("/member/oauth")
    public void accessTokenToMember(@RequestBody  String t){
        System.out.println("token : "+t);
        Map<String,Object> maps = new HashMap<>();
        OAuth2AccessTokenResponse.withToken(t).tokenType(OAuth2AccessToken.TokenType.BEARER).additionalParameters(maps);
        for(String key : maps.keySet()){
            System.out.println(key+":"+maps.get(key));
        }
        System.out.println("end print");
    }
    @PostMapping("/member")
    public void joinMember(@RequestBody MemberRequestDto dto) throws IOException {
        memberService.save(dto);
    }
}
