package com.example.formproject.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CropTypeDto {
    private String type;
    private List<CropDto> crops = new ArrayList<>();
    public void addCrop(CropDto dto){
        this.crops.add(dto);
    }
}
