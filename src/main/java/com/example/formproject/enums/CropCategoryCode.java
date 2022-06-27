package com.example.formproject.enums;

import lombok.Getter;

@Getter
public enum CropCategoryCode {
    식량작물(100),채소류(200),과일류(400);
    private int category;
    CropCategoryCode(int category){
        this.category = category;
    }
    public String findByCode(int num){
        for(CropCategoryCode c : values()){
            if(c.getCategory() == num)
                return c.name();
        }
        return null;
    }
}
