package com.example.formproject.controller;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.LoginDto;
import com.example.formproject.dto.request.MailDto;
import com.example.formproject.dto.request.MemberInfoRequestDto;
import com.example.formproject.dto.request.MemberRequestDto;
import com.example.formproject.dto.response.AccountResponseDto;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Member Api", description = "회원 정보 관련 API(백규현/이경동)")
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService;

    private final OAuthService oAuthService;

    private final ObjectMapper mapper;

    // 로그인
    @PostMapping("/member/login")
    @Operation(summary = "로그인 (백규현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class,example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZCIsImlkIjo2NywiZXhwIjoxNjU3MDk4Mjg5LCJpYXQiOjE2NTcwODc0ODl9._J-jgRNaqMS2_X9aZV0Cj9SgKK_R-VJzzxlexVcj_Gs"))}),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_BADREQUEST, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    public String loginMember(@RequestBody  LoginDto dto, HttpServletResponse response) throws AuthenticationException {
        JwtResponseDto token = memberService.login(dto);

        response.addHeader("Authorization","Bearer "+token.getToken());
        response.addHeader("RefreshToken", "Bearer "+token.getRefreshToken());
        return "Bearer "+token.getToken();
    }
    @PostMapping("/member/email")
    @Operation(summary = "이메일 인증메일 보내기 (백규현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료 (payload: {key : abcdefg...})",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZCIsImlkIjo2NywiZXhwIjoxNjU3MDk4Mjg5LCJpYXQiOjE2NTcwODc0ODl9._J-jgRNaqMS2_X9aZV0Cj9SgKK_R-VJzzxlexVcj_Gs")
                    )}
            ),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)
    })
    public String emailToken(MailDto dto) throws MessagingException {
        String randomChars = RandomString.make(6);
        return emailService.sendHtmlEmail(dto,randomChars);
    }
    @PostMapping("/member/auth")
    @Operation(summary = "Oauth Code를 이용하여 로그인/회원가입 (백규현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZCIsImlkIjo2NywiZXhwIjoxNjU3MDk4Mjg5LCJpYXQiOjE2NTcwODc0ODl9._J-jgRNaqMS2_X9aZV0Cj9SgKK_R-VJzzxlexVcj_Gs")
                    )}
            ),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)
    })
    public String accessTokenToMember(@RequestBody  String t){
        String jwtToken = oAuthService.kakaoLogin(t);
        return jwtToken;
    }
    @PostMapping("/member")
    @Operation(summary = "회원가입 (백규현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료"),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_BADREQUEST, description = "요청 데이터 오류",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    public void joinMember(@RequestBody MemberRequestDto dto) throws IOException {
        memberService.save(dto);
    }

    @GetMapping("/member")
    @Operation(summary = "로그인 정보 조회 (백규현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberResponseDto.class))}),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    public MemberResponseDto getMember(@AuthenticationPrincipal MemberDetail memberDetail) {
        return memberService.makeMemberResponseDto(memberDetail.getMember());
    }

    @PutMapping(value="/member/{memberid}",produces = "application/json; charset=utf-8")
    @Operation(summary = "개인정보 수정 (이경동)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class,example = "회원정보가 수정되었습니다."))}),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    @Parameter(in = ParameterIn.PATH,name = "memberid",description = "사용자 id(database id)",example = "1",required = true)
    public ResponseEntity<?> updateMember(@PathVariable int memberid,
                                          @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                          @RequestPart String data,
                                          @AuthenticationPrincipal MemberDetail memberDetails) throws JsonProcessingException {
        MemberInfoRequestDto requestDto = mapper.readValue(data,MemberInfoRequestDto.class);
        return memberService.updateMember(memberid, profileImage, requestDto, memberDetails.getUsername());
    }
}
