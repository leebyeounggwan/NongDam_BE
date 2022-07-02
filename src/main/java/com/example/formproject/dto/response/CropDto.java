package com.example.formproject.dto.response;

import com.example.formproject.entity.Crop;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropDto {
    private int id;
    private String name;
    public CropDto(Crop crop){
        this.id = crop.getId();
        this.name = crop.getName();
    }
}
