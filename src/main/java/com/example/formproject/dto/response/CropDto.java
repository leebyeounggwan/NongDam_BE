package com.example.formproject.dto.response;

import com.example.formproject.entity.Crop;
import com.example.formproject.enums.CropCategoryCode;
import com.example.formproject.enums.CropTypeCode;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropDto {
    private int id;
    private String name;
    private String category;
    private String type;
    public CropDto(Crop crop){
        this.id = crop.getId();
        this.name = crop.getName();
        this.category = CropCategoryCode.findByCode(crop.getCategory());
        this.type = CropTypeCode.findByCode(crop.getType()).name();
    }
}
