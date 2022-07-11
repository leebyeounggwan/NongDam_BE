package com.example.formproject.service;

import com.example.formproject.dto.request.ScheduleRequestDto;
import com.example.formproject.dto.response.ScheduleResponseDto;
import com.example.formproject.entity.Member;
import com.example.formproject.entity.Schedule;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.repository.ScheduleRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final CropRepository cropRepository;
    @Transactional
    public ScheduleResponseDto save(Member member, ScheduleRequestDto dto){
        return new ScheduleResponseDto(scheduleRepository.save(dto.build(member,cropRepository)));
    }
    @Transactional
    public ScheduleResponseDto save(long scheduleId,ScheduleRequestDto dto){
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(()->new IllegalArgumentException("일정 정보를 찾을 수 없습니다"));
        schedule.update(dto,cropRepository);
        return new ScheduleResponseDto(scheduleRepository.save(schedule));
    }
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findScheduleOfWeek(Member member){
        LocalDateTime now = LocalDate.now().atTime(23,59,59);
        LocalDateTime lastWeek = now.minusWeeks(1L).toLocalDate().atTime(0,0,0);
        List<Schedule> schedules = scheduleRepository.findScheduleLastWeek(member.getId(),now,lastWeek);
        List<ScheduleResponseDto> ret = new ArrayList<>();
        schedules.stream().forEach(e-> ret.add(new ScheduleResponseDto(e)));
        return ret;
    }
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findScheduleOfMonth(Member member,int year,int month){
        List<Schedule> schedules = scheduleRepository.findScheduleOfMonth(member.getId(),year,month);
        List<ScheduleResponseDto> ret = new ArrayList<>();
        schedules.stream().forEach(e-> ret.add(new ScheduleResponseDto(e)));
        return ret;
    }
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findScheduleOfDay(Member member){
        LocalDate now = LocalDate.now();
        List<Schedule> schedules = scheduleRepository.findScheduleOfDay(member.getId(),now.atTime(0,0,0),now.atTime(23,59,59));
        List<ScheduleResponseDto> ret = new ArrayList<>();
        schedules.stream().forEach(e-> ret.add(new ScheduleResponseDto(e)));
        return ret;
    }
}
