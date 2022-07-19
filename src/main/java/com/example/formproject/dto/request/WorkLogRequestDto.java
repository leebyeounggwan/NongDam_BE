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
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class WorkLogRequestDto {
    @Schema(type = "String", example = "제목1")
    private String title;
    @Schema(type = "String", example = "2022-07-06")
    private String date;
    @Schema(type = "int", example = "1")
    private int workTime;
    @Schema(type = "int", example = "1")
    private int crop;
    @Schema(type = "String", example = "오늘은 000을 했다.")
    private String memo;
    @Schema(type = "int", example = "100")
    private Long harvest;
    private List<SubMaterialRequestDto> subMaterial;

    private List<String> images;

    public WorkLog build(Member member, CropRepository repository) {
        WorkLog workLog = WorkLog.builder()
                .title(this.title)
                .date(LocalDate.parse(this.date, FinalValue.DAY_FORMATTER))
                .workTime(this.workTime)
                .memo(this.memo)
                .crop(repository.findById(crop).orElseThrow(() -> new IllegalArgumentException("작물 정보를 찾을 수 없습니다.")))
                .harvest(this.harvest)
                .member(member)
                .build();
        workLog.setQuarter();
        subMaterial.stream().forEach(e -> {
            workLog.addSubMaterial(e.build());
        });
        return workLog;
    }
}
