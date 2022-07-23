package com.example.formproject.controller;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.AccountRequestDto;
import com.example.formproject.dto.response.AccountResponseDto;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.AccountBookService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name="Account Api",description = "장부 관련 API(백규현)")
public class AccountBookController {

    private final AccountBookService service;
    @GetMapping("/accountbook/{yearMonth}")
    @Operation(summary = "한달치 장부 기록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AccountResponseDto.class))) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_BADREQUEST, description = "요청데이터 오류",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    @Parameter(in = ParameterIn.PATH,name = "yearMonth",description = "년월 정보",example = "2022-07",required = true)
    public List<AccountResponseDto> findByMonth(@AuthenticationPrincipal MemberDetail detail, @PathVariable String yearMonth){
        String[] tmp = yearMonth.split("-");
        return service.findByMonth(detail.getMember(),Integer.parseInt(tmp[0]),Integer.parseInt(tmp[1]));
    }
    @GetMapping("/accountbook")
    @Operation(summary = "최근 10개 장부 기록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AccountResponseDto.class))) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    public List<AccountResponseDto> findByLimit(@AuthenticationPrincipal MemberDetail detail){
        return service.findByLimits(detail.getMember(),10);
    }
    @PostMapping("/accountbook")
    @Operation(summary = "장부 기록 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    public AccountResponseDto saveAccount(@AuthenticationPrincipal MemberDetail detail, @RequestBody AccountRequestDto dto){
        return service.save(detail.getMember(),dto);
    }
    @PutMapping("/accountbook/{accountId}")
    @Operation(summary = "장부 기록 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    @Parameter(in = ParameterIn.PATH,name = "accountId",description = "장부 id",example = "1",required = true)
    public AccountResponseDto editAccount(@PathVariable Long accountId,@RequestBody AccountRequestDto dto){
        return service.save(accountId.longValue(),dto);
    }
    @DeleteMapping("/accountbook/{accountId}")
    @Operation(summary = "장부 기록 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료"),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    @Parameter(in = ParameterIn.PATH,name = "accountId",description = "장부 id",example = "1",required = true)
    public void deleteAccount(@PathVariable Long accountId){
        service.delete(accountId);
    }
    @ExceptionHandler({NumberFormatException.class,IndexOutOfBoundsException.class})
    public ResponseEntity<String> badRequest(){
        return new ResponseEntity("요청데이터가 잘못되었습니다.",HttpStatus.BAD_REQUEST);
    }
}
