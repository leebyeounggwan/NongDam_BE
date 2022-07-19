package com.example.formproject.dto.response;

import com.example.formproject.entity.Images;
import com.example.formproject.entity.SubMaterial;
import com.example.formproject.entity.WorkLog;
import io.lettuce.core.pubsub.PubSubMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class WorkLogResponseDto {
    @Schema(type = "PK", example = "1")
    private Long id;
    @Schema(type = "String", example = "제목1")
    private String title;
    @Schema(type = "String", example = "2022-07-06")
    private LocalDate date;
    @Schema(type = "int", example = "1")
    private int workTime;
    @Schema(type = "int", example = "1")
    private int crop;
    @Schema(type = "String", example = "오늘은 000을 했다.")
    private String memo;
    @Schema(type = "int", example = "100")
    private Long harvest;

    private List<SubMaterial> subMaterial = new ArrayList<>();

    private List<String> images = new ArrayList<>();

    public WorkLogResponseDto(WorkLog workLog) {
        this.id = workLog.getId();
        this.title = workLog.getTitle();
        this.date = workLog.getDate();
        this.workTime = workLog.getWorkTime();
        this.crop = workLog.getCrop().getId();
        this.memo = workLog.getMemo();
        this.harvest = workLog.getHarvest();
        this.subMaterial.addAll(workLog.getSubMaterials());
        for (Images image : workLog.getImages()) this.images.add(image.getUrl());
    }
}