package com.example.formproject.service;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.ScheduleRequestDto;
import com.example.formproject.dto.response.ScheduleResponseDto;
import com.example.formproject.entity.Member;
import com.example.formproject.entity.Schedule;
import com.example.formproject.exception.WrongArgumentException;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.repository.ScheduleRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final CropRepository cropRepository;
    @Transactional
    public ScheduleResponseDto save(Member member, ScheduleRequestDto dto) throws WrongArgumentException {
        return new ScheduleResponseDto(scheduleRepository.save(dto.build(member,cropRepository)));
    }
    @Transactional
    public ScheduleResponseDto save(long scheduleId,ScheduleRequestDto dto) throws WrongArgumentException {
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
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startTime = LocalDateTime.of(year,month,1,0,0,0);
        startTime = startTime.minusDays(FinalValue.getBackDayOfWeekValue(startTime.getDayOfWeek()));
        LocalDateTime endTime = yearMonth.atEndOfMonth().atTime(23,59,59);
        endTime = endTime.plusDays(FinalValue.getForwardDayOfWeekValue(endTime.getDayOfWeek()));
        List<Schedule> schedules = scheduleRepository.findScheduleOfMonth(member.getId(),startTime,endTime);
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
    @Transactional
    public void delete(long id){
        scheduleRepository.deleteById(id);
    }
}
