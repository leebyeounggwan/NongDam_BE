package com.example.formproject.dto.request;

import com.example.formproject.FinalValue;
import com.example.formproject.entity.Member;
import com.example.formproject.entity.WorkLog;
import com.example.formproject.repository.CropRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class WorkLogRequestDto {
    private String startTime;
    private String endTime;
    private int crop;
    private String memo;
    private int harvest;
    private List<SubMaterialRequestDto> subMaterial;

    private List<String> pictures;

    public WorkLog build(Member member, CropRepository repository){
        WorkLog workLog = WorkLog.builder()
                .startTime(LocalDateTime.parse(this.startTime, FinalValue.DAYTIME_FORMATTER))
                .endTime(LocalDateTime.parse(this.endTime, FinalValue.DAYTIME_FORMATTER))
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
