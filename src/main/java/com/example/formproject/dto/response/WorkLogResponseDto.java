package com.example.formproject.dto.response;

import com.example.formproject.entity.SubMaterial;
import com.example.formproject.entity.WorkLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkLogResponseDto {
    @Schema(type = "PK", example = "1")
    private Long id;
    @Schema(type = "String", example = "제목1")
    private String title;
    @Schema(type = "String", example = "2022-07-06")
    private String date;
    @Schema(type = "int", example = "1")
    private int workTime;
    @Schema(type = "String", example = "오늘은 000을 했다.")
    private String memo;
    @Schema(type = "int", example = "100")
    private Long harvest;
    private CropDto crop;
    private List<SubMaterialResponseDto> subMaterial = new ArrayList<>();
    private List<String> images = new ArrayList<>();

    private SubWorkLogResponseDto pre;

    private SubWorkLogResponseDto next;

    public WorkLogResponseDto(WorkLog workLog, CropDto cropDto) {
        this.id = workLog.getId();
        this.title = workLog.getTitle();
        this.date = workLog.getDate().toString();
        this.workTime = workLog.getWorkTime();
        this.memo = workLog.getMemo();
        this.harvest = workLog.getHarvest();
        this.crop = cropDto;
        for(SubMaterial material : workLog.getSubMaterials())
            this.subMaterial.add(new SubMaterialResponseDto(material));
        this.images.addAll(workLog.getImages());
    }
    public void setPreviousWorkLogInfo(SubWorkLogResponseDto pre){
        this.pre = pre;
    }
    public void setNextWorkLogInfo(SubWorkLogResponseDto next){
        this.next = next;
    }
}