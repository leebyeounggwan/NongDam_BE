package com.example.formproject.controller;

import com.example.formproject.dto.request.AccountRequestDto;
import com.example.formproject.dto.response.AccountResponseDto;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.AccountBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountBookController {

    private final AccountBookService service;

    @GetMapping("/accountbook/{yearMonth}")
    public List<AccountResponseDto> findByMonth(@AuthenticationPrincipal MemberDetail detail, @PathVariable String yearMonth){
        String[] tmp = yearMonth.split("-");
        return service.findByMonth(detail.getMember(),Integer.parseInt(tmp[0]),Integer.parseInt(tmp[1]));
    }
//    @GetMapping("/accountbook")
//    public List<AccountResponseDto> findByLimit(@AuthenticationPrincipal MemberDetail detail){
//        return service.findByLimits(detail.getMember(),10);
//    }
    @PostMapping("/accountbook")
    public AccountResponseDto saveAccount(@AuthenticationPrincipal MemberDetail detail, @RequestBody AccountRequestDto dto){
        return service.save(detail.getMember(),dto);
    }
    @PutMapping("/accountbook/{accountId}")
    public AccountResponseDto editAccount(@PathVariable Long accountId,@RequestBody AccountRequestDto dto){
        return service.save(accountId.longValue(),dto);
    }
    @DeleteMapping("/accountbook/{accountId}")
    public void deleteAccount(@PathVariable Long accountId){
        service.getAccountBookRepository().deleteById(accountId);
    }
}
