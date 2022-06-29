package com.example.formproject.controller;

import com.example.formproject.dto.response.CircleChartDto;
import com.example.formproject.dto.response.LineChartDataDto;
import com.example.formproject.dto.response.LineChartDto;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.AccountBookService;
import com.example.formproject.service.WorkLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResultController {
    private final AccountBookService accountBookService;

    private final WorkLogService workLogService;

    @GetMapping("/totalharvest")
    public LineChartDto getHarvestResult(@AuthenticationPrincipal MemberDetail detail){
        return workLogService.getHarvestData(detail.getMember());
    }

    @GetMapping("/sales")
    public LineChartDto getAccountResult(@AuthenticationPrincipal MemberDetail detail){
        return accountBookService.getResults(detail.getMember());
    }

    @GetMapping("/income")
    public CircleChartDto getIncomeOfYear(@AuthenticationPrincipal MemberDetail detail){
        return accountBookService.getIncomeResult(detail.getMember());
    }

    @GetMapping("/expense")
    public CircleChartDto getExpenseOfYear(@AuthenticationPrincipal MemberDetail detail){
        return accountBookService.getExpenseResult(detail.getMember());
    }
}
