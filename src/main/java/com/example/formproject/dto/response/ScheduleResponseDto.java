package com.example.formproject.dto.response;

import com.example.formproject.FinalValue;
import com.example.formproject.entity.Schedule;
import lombok.Getter;

@Getter
public class ScheduleResponseDto {
    private long id;
    private String startTime;
    private String endTime;
    private String crop;
    private String toDo;
    public ScheduleResponseDto(Schedule schedule){
        this.id = schedule.getId();
        this.startTime = schedule.getStartTime().format(FinalValue.DAYTIME_FORMATTER);
        this.endTime = schedule.getEndTime().format(FinalValue.DAYTIME_FORMATTER);
        this.crop = schedule.getCrop().getName();
        this.toDo = schedule.getToDo();
    }
}
