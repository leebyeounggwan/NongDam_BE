package com.example.formproject.dto.response;

import com.example.formproject.entity.Crop;
import com.example.formproject.enums.CropCategoryCode;
import com.example.formproject.enums.CropTypeCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropDto {
    @Schema(type = "int", example = "1")
    private int id;
    @Schema(type = "String", example = "백미")
    private String name;
    @Schema(type = "String", example = "식량작물")
    private String category;
    @Schema(type = "String", example = "쌀")
    private String type;

    public CropDto(Crop crop) {
        this.id = crop.getId();
        this.name = crop.getName();
        this.category = CropCategoryCode.findByCode(crop.getCategory());
        this.type = CropTypeCode.findByCode(crop.getType()).name();
    }
}