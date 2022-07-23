package com.example.formproject.dto.response;

import com.example.formproject.entity.SubMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@AllArgsConstructor
public class SubMaterialResponseDto {
    private long id;

    private int type;

    private String product;

    private String use;

    public SubMaterialResponseDto(SubMaterial material){
        this.id = material.getId();
        this.type = material.getType();
        this.product = material.getProduct();
        this.use = material.getUse();
    }
}
