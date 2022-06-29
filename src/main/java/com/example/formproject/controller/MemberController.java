package com.example.formproject.controller;

import com.example.formproject.dto.request.LoginDto;
import com.example.formproject.dto.request.MailDto;
import com.example.formproject.dto.request.MemberInfoRequestDto;
import com.example.formproject.dto.request.MemberRequestDto;
import com.example.formproject.entity.Member;
import com.example.formproject.exception.AuthenticationException;
import com.example.formproject.repository.MemberRepository;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.EmailService;
import com.example.formproject.service.MemberService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.bind.annotation.*;

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
    private final MemberRepository repository;
    private final MemberService memberService;
    private final EmailService emailService;

    // 로그인
    @PostMapping("/member/login")
    public String loginMember(@RequestBody LoginDto dto, HttpServletResponse response) throws AuthenticationException {
        String token = memberService.login(dto);

        response.addHeader("Authorization", "Bearer " + token);
        return "Bearer " + token;
    }

    @PostMapping("/member/email")
    public String emailToken(MailDto dto) throws MessagingException {
        String randomChars = RandomString.make(6);
        return emailService.sendHtmlEmail(dto, randomChars);
    }

    @PostMapping("/member/oauth")
    public void accessTokenToMember(@RequestBody String t) {
        System.out.println("token : " + t);
        Map<String, Object> maps = new HashMap<>();
        OAuth2AccessTokenResponse.withToken(t).tokenType(OAuth2AccessToken.TokenType.BEARER).additionalParameters(maps);
        for (String key : maps.keySet()) {
            System.out.println(key + ":" + maps.get(key));
        }
        System.out.println("end print");
    }

    @PostMapping("/member")
    public void joinMember(@RequestBody MemberRequestDto dto) throws IOException {
        memberService.save(dto);
    }

    @GetMapping("/member}")
    public List<Member> getMember(@AuthenticationPrincipal MemberDetail memberDetail) {
        return repository.findAllByMember(memberDetail.getMember());
    }

//    @PutMapping("/member/{memberid}")
//    public ResponseEntity<?> updateMember(@PathVariable int memberid,
//                                          @RequestBody MemberInfoRequestDto requestDto,
//                                          @AuthenticationPrincipal MemberDetail memberDetails) {
//        String username = memberDetails.getUsername();
//
//        return memberService.updateMember(memberid, requestDto, username);
//    }
}
