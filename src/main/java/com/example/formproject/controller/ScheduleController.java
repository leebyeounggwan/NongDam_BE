package com.example.formproject.controller;

import com.example.formproject.dto.request.ScheduleRequestDto;
import com.example.formproject.dto.response.ScheduleResponseDto;
import com.example.formproject.entity.Member;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    @GetMapping("/schedule")
    public List<ScheduleResponseDto> getSchedules(@AuthenticationPrincipal MemberDetail detail){
        return scheduleService.findScheduleOfWeek(detail.getMember());
    }
    @GetMapping("/schedule/{yearMonth}")
    public List<ScheduleResponseDto> getMonthSchedules(@AuthenticationPrincipal MemberDetail detail,@PathVariable String yearMonth){
        String[] tmp= yearMonth.split("-");
        return scheduleService.findScheduleOfMonth(detail.getMember(),Integer.parseInt(tmp[0]),Integer.parseInt(tmp[1]));
    }
    @GetMapping("/schedule/today")
    public List<ScheduleResponseDto> getDaySchedules(@AuthenticationPrincipal MemberDetail detail){
        return scheduleService.findScheduleOfDay(detail.getMember());
    }

    @PostMapping("/schedule")
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto dto, @AuthenticationPrincipal MemberDetail detail){
        return scheduleService.save(detail.getMember(),dto);
    }

    @PutMapping("/schedule/{scheduleId}")
    public ScheduleResponseDto editSchedule(@PathVariable Long scheduleId,ScheduleRequestDto dto){
        return scheduleService.save(scheduleId,dto);
    }
    @DeleteMapping("/schedule/{scheduleId}")
    public void deleteSchedule(@PathVariable Long scheduleId){
        scheduleService.getScheduleRepository().deleteById(scheduleId);
    }
}
