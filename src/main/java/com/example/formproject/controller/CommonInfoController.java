package com.example.formproject.controller;

import com.example.formproject.dto.response.CropCategoryDto;
import com.example.formproject.service.CropService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommonInfoController {
    private final CropService cropService;
    @GetMapping("/crops")
    public List<CropCategoryDto> getAllCrops(){
        return cropService.findAllData();
    }
}
