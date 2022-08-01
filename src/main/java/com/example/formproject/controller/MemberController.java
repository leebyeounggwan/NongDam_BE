package com.example.formproject.controller;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.LoginDto;
import com.example.formproject.dto.request.MemberInfoRequestDto;
import com.example.formproject.dto.request.MemberRequestDto;
import com.example.formproject.dto.request.PasswordChangeDto;
import com.example.formproject.dto.response.JwtResponseDto;
import com.example.formproject.dto.response.MemberResponseDto;
import com.example.formproject.exception.AuthenticationException;
import com.example.formproject.exception.EmailConfirmException;
import com.example.formproject.exception.WrongArgumentException;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.MemberService;
import com.example.formproject.service.OAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "Member Api", description = "회원 정보 관련 API(백규현/이경동)")
public class MemberController {
    private final MemberService memberService;
    private final OAuthService oAuthService;
    private final ObjectMapper mapper;

    // 로그인
    @PostMapping("/member/login")
    @Operation(summary = "로그인 (백규현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZCIsImlkIjo2NywiZXhwIjoxNjU3MDk4Mjg5LCJpYXQiOjE2NTcwODc0ODl9._J-jgRNaqMS2_X9aZV0Cj9SgKK_R-VJzzxlexVcj_Gs"))}),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_BADREQUEST, description = "로그인 필요", content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류", content = @Content)})
    public String loginMember(@RequestBody LoginDto dto, HttpServletResponse response) throws EmailConfirmException, WrongArgumentException {
        JwtResponseDto token = memberService.login(dto, response);
        return "Bearer " + token.getToken();
    }

    @GetMapping("/member/email")
    @Operation(summary = "이메일 인증완료 (백규현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료 (payload: {key : abcdefg...})",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZCIsImlkIjo2NywiZXhwIjoxNjU3MDk4Mjg5LCJpYXQiOjE2NTcwODc0ODl9._J-jgRNaqMS2_X9aZV0Cj9SgKK_R-VJzzxlexVcj_Gs")
                    )}
            ),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류", content = @Content)
    })
    public void emailToken(@RequestParam("id") Integer id, HttpServletResponse response) throws Exception {
        memberService.enableMember(id);
        response.sendRedirect(FinalValue.FRONT_URL + "/login");
    }

    @PostMapping("/member/auth")
    @Operation(summary = "Oauth Code를 이용하여 로그인/회원가입 (백규현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZCIsImlkIjo2NywiZXhwIjoxNjU3MDk4Mjg5LCJpYXQiOjE2NTcwODc0ODl9._J-jgRNaqMS2_X9aZV0Cj9SgKK_R-VJzzxlexVcj_Gs")
                    )}
            ),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류", content = @Content)
    })
    public String accessTokenToMember(@RequestBody String t, HttpServletResponse response) throws AuthenticationException {
        JwtResponseDto dto = oAuthService.kakaoLogin(t, response);

        return dto.getToken();
    }

    @PostMapping("/member")
    @Operation(summary = "회원가입 (백규현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료"),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_BADREQUEST, description = "요청 데이터 오류", content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류", content = @Content)})
    public void joinMember(@RequestBody MemberRequestDto dto) throws MessagingException, WrongArgumentException {
        memberService.save(dto);
    }

    @GetMapping("/member")
    @Operation(summary = "로그인 정보 조회 (백규현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberResponseDto.class))}),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요", content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류", content = @Content)})
    public MemberResponseDto getMember(@AuthenticationPrincipal MemberDetail memberDetail) throws AuthenticationException {
        if(memberDetail == null)
            throw new AuthenticationException("로그인이 필요한 서비스 입니다.","member info");
        return memberService.makeMemberResponseDto(memberDetail.getMember());
    }

    @PutMapping(value = "/member")
    @Operation(summary = "개인정보 수정 (이경동)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "회원정보가 수정되었습니다."))}),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요", content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류", content = @Content)})
    @Parameter(in = ParameterIn.PATH, name = "memberid", description = "사용자 id(database id)", example = "1", required = true)
    public ResponseEntity<?> updateMember(@RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                          @RequestPart String data,
                                          @AuthenticationPrincipal MemberDetail memberDetails) throws JsonProcessingException, AuthenticationException {
        if(memberDetails == null)
            throw new AuthenticationException("로그인이 필요한 서비스 입니다.","update member");
        MemberInfoRequestDto requestDto = mapper.readValue(data, MemberInfoRequestDto.class);
        return memberService.updateMember(memberDetails.getMember().getProfileImage(), memberDetails.getMember().getId(), profileImage, requestDto, memberDetails.getMember().getEmail());
    }

    @PutMapping("/member/password")
    public void changePassword(@AuthenticationPrincipal MemberDetail detail, @RequestBody PasswordChangeDto dto) throws AuthenticationException, WrongArgumentException {
        if(detail == null)
            throw new AuthenticationException("로그인이 필요한 서비스 입니다.","change password");
        memberService.changePassword(detail.getMember(),dto);
    }
}