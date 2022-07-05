package com.example.formproject.controller;

import com.example.formproject.dto.request.LoginDto;
import com.example.formproject.dto.request.MailDto;
import com.example.formproject.dto.request.MemberInfoRequestDto;
import com.example.formproject.dto.request.MemberRequestDto;
import com.example.formproject.dto.response.JwtResponseDto;
import com.example.formproject.dto.response.MemberResponseDto;
import com.example.formproject.entity.Member;
import com.example.formproject.exception.AuthenticationException;
import com.example.formproject.repository.MemberRepository;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.EmailService;
import com.example.formproject.service.MemberService;
import com.example.formproject.service.OAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService;

    private final OAuthService oAuthService;

    private final ObjectMapper mapper;

    // 로그인
    @PostMapping("/member/login")
    public String loginMember(@RequestBody  LoginDto dto, HttpServletResponse response) throws AuthenticationException {
        JwtResponseDto token = memberService.login(dto);

        response.addHeader("Authorization","Bearer "+token.getToken());
        response.addHeader("RefreshToken", "Bearer "+token.getRefreshToken());
        return "Bearer "+token.getToken();
    }
    @PostMapping("/member/email")
    public String emailToken(MailDto dto) throws MessagingException {
        String randomChars = RandomString.make(6);
        return emailService.sendHtmlEmail(dto,randomChars);
    }
    @PostMapping("/member/auth")
    public String accessTokenToMember(@RequestBody  String t){
        String jwtToken = oAuthService.kakaoLogin(t);
        return jwtToken;
    }
    @PostMapping("/member")
    public void joinMember(@RequestBody MemberRequestDto dto) throws IOException {
        memberService.save(dto);
    }

    @GetMapping("/member")
    public MemberResponseDto getMember(@AuthenticationPrincipal MemberDetail memberDetail) {
        return memberService.makeMemberResponseDto(memberDetail.getMember());
    }

    @PutMapping("/member/{memberid}")
    public ResponseEntity<?> updateMember(@PathVariable int memberid,
                                          @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                          @RequestPart String data,
                                          @AuthenticationPrincipal MemberDetail memberDetails) throws JsonProcessingException {
        MemberInfoRequestDto requestDto = mapper.readValue(data,MemberInfoRequestDto.class);
        return memberService.updateMember(memberid, profileImage, requestDto, memberDetails.getUsername());
    }
}
