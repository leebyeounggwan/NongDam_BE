package com.example.formproject.dto.request;

import com.example.formproject.FinalValue;
import com.example.formproject.entity.Member;
import com.example.formproject.entity.Schedule;
import com.example.formproject.exception.WrongArgumentException;
import com.example.formproject.repository.CropRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequestDto {
    @Schema(type = "int",example = "1")
    private int cropId;
    @Schema(type = "String",example = "2022-07-06 10:00")
    private String startTime;
    @Schema(type = "String",example = "2022-07-06 10:00")
    private String endTime;
    @Schema(type = "String",example = "할일")
    private String toDo;

    public Schedule build(Member member, CropRepository repository) throws WrongArgumentException {
        LocalDateTime startTime = LocalDateTime.parse(this.startTime, FinalValue.DAYTIME_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(this.endTime, FinalValue.DAYTIME_FORMATTER);
        if(startTime.isAfter(endTime))
            throw new WrongArgumentException("잘못된 입력입니다.","시작/끝 시간");
        return Schedule.builder()
                .crop(repository.findById(this.cropId).get())
                .startTime(startTime)
                .endTime(endTime)
                .toDo(this.toDo)
                .member(member)
                .build();
    }
}
