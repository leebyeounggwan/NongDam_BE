package com.example.formproject.dto.response;

import com.example.formproject.FinalValue;
import com.example.formproject.entity.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ScheduleResponseDto {
    @Schema(type = "long",example = "1")
    private long id;
    @Schema(type = "String",example = "2022-07-06 10:00")
    private String startTime;
    @Schema(type = "String",example = "2022-07-06 10:00")
    private String endTime;
    @Schema(type = "String",example = "백미")
    private String crop;
    @Schema(type = "int",example = "1")
    private int cropId;
    @Schema(type = "String",example = "물뿌리기")
    private String toDo;
    public ScheduleResponseDto(Schedule schedule){
        this.id = schedule.getId();
        this.startTime = schedule.getStartTime().format(FinalValue.DAYTIME_FORMATTER);
        this.endTime = schedule.getEndTime().format(FinalValue.DAYTIME_FORMATTER);
        this.crop = schedule.getCrop().getName();
        this.cropId = schedule.getCrop().getId();
        this.toDo = schedule.getToDo();
    }
}
