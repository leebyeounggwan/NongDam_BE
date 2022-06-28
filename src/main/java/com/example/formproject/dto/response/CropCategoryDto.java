package com.example.formproject.dto.response;

import com.example.formproject.entity.Crop;
import com.example.formproject.enums.CropTypeCode;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CropCategoryDto {
    private String category;
    private List<CropTypeDto> types = new ArrayList<>();

    public void addCrop(Crop c){
        CropTypeDto dto = null;
        try {
            dto = types.stream().filter(e -> e.getType().equals(CropTypeCode.getNameByCode(c.getType()).name())).collect(Collectors.toList()).get(0);
        }catch (IndexOutOfBoundsException e){
            dto = new CropTypeDto();
            dto.setType(CropTypeCode.getNameByCode(c.getType()).name());
            types.add(dto);
        }
        dto.addCrop(CropDto.builder().id(c.getId()).name(c.getName()).build());
    }
}
