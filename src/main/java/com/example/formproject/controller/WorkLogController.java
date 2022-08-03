package com.example.formproject.controller;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.WorkLogRequestDto;
import com.example.formproject.dto.response.WorkLogResponseDto;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.WorkLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "WorkLog Api", description = "작업 결과 관련 Api(설계 진행중,이경동)")
@Slf4j
public class WorkLogController {
    private final WorkLogService workLogService;

    @PostMapping(value = "/worklog", consumes = {"multipart/form-data"})
    @Operation(summary = "작업일지 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요", content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류", content = @Content)})
    public void createWorkLog(@AuthenticationPrincipal MemberDetail details,
                              @RequestPart String data,
                              @RequestPart(required = false) List<MultipartFile> images) {
        ObjectMapper mapper = new ObjectMapper();
        WorkLogRequestDto dto;
        try {
            dto = mapper.readValue(data, WorkLogRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        workLogService.createWorkLog(details.getMember(), dto, images);
    }

    @GetMapping("/worklog/{worklogid}")
    public WorkLogResponseDto getWorkLog(@PathVariable Long worklogid,
                                         @AuthenticationPrincipal MemberDetail detail) {
        String userEmail = detail.getUsername();
        return workLogService.getWorkLogDetails(worklogid, detail.getMember().getId());
    }

    @GetMapping("/worklog")
    @Operation(summary = "작업일지 전체조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkLogRequestDto.class))}),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요", content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류", content = @Content)})
    public List<WorkLogResponseDto> getWorkLogList(@AuthenticationPrincipal MemberDetail detail) throws JsonProcessingException {
        return workLogService.getWorkLogList(detail);
    }

    @GetMapping("/workloglist")
    @Operation(summary = "작업일지 전체조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkLogRequestDto.class))}),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요", content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류", content = @Content)})
    public Page<WorkLogResponseDto> getWorkLogListPage(@AuthenticationPrincipal MemberDetail detail) throws JsonProcessingException {
        return workLogService.getWorkLogListPage(detail);
    }

    @DeleteMapping("/worklog/{worklogid}")
    public void deleteWorkLog(@PathVariable Long worklogid, @AuthenticationPrincipal MemberDetail details) {
        workLogService.deleteWorkLog(worklogid, details);
    }

    @PutMapping(value = "/worklog/{worklogid}/update", consumes = {"multipart/form-data"})
    public void updateWorkLog(@PathVariable Long worklogid,
                              @RequestPart String data,
                              @AuthenticationPrincipal MemberDetail details,
                              @RequestPart(required = false) List<MultipartFile> images) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        WorkLogRequestDto dto;
        try {
            dto = mapper.readValue(data, WorkLogRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        workLogService.updateWorkLog(worklogid, dto, details, images);
    }
}