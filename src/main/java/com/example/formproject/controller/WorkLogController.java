package com.example.formproject.controller;

import com.example.formproject.dto.request.WorkLogRequestDto;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.WorkLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WorkLogController {
    private final WorkLogService workLogService;
    @PostMapping(value = "/worklog",consumes = {"multipart/form-data"})
    public void saveWorkLog(@AuthenticationPrincipal MemberDetail detail, @RequestPart(required = false) String data,@RequestPart List<MultipartFile> images) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        WorkLogRequestDto dto = mapper.readValue(data,WorkLogRequestDto.class);
        workLogService.saveWork(detail.getMember(),dto,images);
    }
}
