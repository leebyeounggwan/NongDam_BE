package com.example.formproject.dto.request;

import com.example.formproject.FinalValue;
import com.example.formproject.entity.Member;
import com.example.formproject.entity.WorkLog;
import com.example.formproject.repository.CropRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class WorkLogRequestDto {
    @Schema(type = "String",example = "2022-07-06 10:00")
    private String startTime;
    @Schema(type = "String",example = "2022-07-06 10:00")
    private String endTime;
    @Schema(type = "int",example = "1")
    private int crop;
    @Schema(type = "String",example = "오늘은 000을 했다.")
    private String memo;
    @Schema(type = "int",example = "100")
    private int harvest;
    private List<SubMaterialRequestDto> subMaterial;

    public WorkLog build(Member member, CropRepository repository){
        WorkLog workLog = WorkLog.builder()
                .date(LocalDate.parse(this.startTime, FinalValue.DAY_FORMATTER))
                .memo(this.memo)
                .crop(repository.findById(crop).orElseThrow(()->new IllegalArgumentException("작물 정보를 찾을 수 없습니다.")))
                .harvest(this.harvest)
                .member(member)
                .build();
        workLog.setQuarter();
        subMaterial.stream().forEach(e->{workLog.addSubMaterial(e.build());});
        return workLog;
    }
}
