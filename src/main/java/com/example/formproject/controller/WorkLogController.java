package com.example.formproject.controller;

import com.example.formproject.dto.request.WorkLogRequestDto;
import com.example.formproject.dto.response.WorkLogResponseDto;
import com.example.formproject.repository.WorkLogRepository;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.WorkLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "WorkLog Api", description = "작업 결과 관련 Api(설계 진행중,이경동)")
public class WorkLogController {
    private final WorkLogRepository workLogRepository;
    private final WorkLogService workLogService;
//    private final WorkLogResponseDto workLogResponseDto;

    @PostMapping(value = "/worklog", consumes = {"multipart/form-data"})
    public void createWorkLog(@AuthenticationPrincipal MemberDetail detail,
                              @RequestPart(required = false) String data,
                              @RequestPart List<MultipartFile> images) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        WorkLogRequestDto dto = mapper.readValue(data, WorkLogRequestDto.class);
        workLogService.createWorkLog(detail.getMember(), dto, images);
    }

    @GetMapping("/worklog/{worklogid}")
    public WorkLogResponseDto getWorkLog(@PathVariable Long worklogid,
                                                   @AuthenticationPrincipal MemberDetail detail) {
        String userEmail = detail.getUsername();
        return workLogService.getWorkLogDetails(worklogid, userEmail);
    }

    @GetMapping("/worklog")
    public List<WorkLogResponseDto> getWorkLogList(@AuthenticationPrincipal MemberDetail detail) {
        return workLogService.getWorkLogList(detail);
    }

    @DeleteMapping("/worklog/{worklogid}")
    public void deleteWorkLog(@PathVariable Long worklogid, @AuthenticationPrincipal MemberDetail detail) {
        String userEmail = detail.getUsername();
        workLogService.deleteWorkLog(worklogid, userEmail);
    }

//    @PatchMapping(value = "/worklog/{worklogid}", consumes = {"multipart/form-data"})
//    public List<WorkLogResponseDto> updateWorkLog(@PathVariable Long worklogid, @AuthenticationPrincipal MemberDetail detail) {
//        String userEmail = detail.getUsername();
//        return workLogService.updateWorkLog(worklogid, userEmail);
//    }
}