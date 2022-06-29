package com.example.formproject.dto.request;

import com.example.formproject.FinalValue;
import com.example.formproject.entity.Member;
import com.example.formproject.entity.Schedule;
import com.example.formproject.repository.CropRepository;
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
    private int crop;
    private String startTime;
    private String endTime;
    private String toDo;

    public Schedule build(Member member, CropRepository repository){
        return Schedule.builder()
                .crop(repository.findById(this.crop).get())
                .startTime(LocalDateTime.parse(this.startTime, FinalValue.DAYTIME_FORMATTER))
                .endTime(LocalDateTime.parse(this.endTime, FinalValue.DAYTIME_FORMATTER))
                .toDo(this.toDo)
                .member(member)
                .build();
    }
}
