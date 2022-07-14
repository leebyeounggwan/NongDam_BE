package com.example.formproject.dto.request;

import com.example.formproject.dto.response.CropDto;
import com.example.formproject.entity.Crop;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class MemberInfoRequestDto {
    @Schema(type = "String", example = "example nickname")
    private String nickname;
    @Schema(type = "String", example = "example address")
    private String address;
    @Schema(type = "int", example = "1101")
    private int countryCode;
    @Schema(type = "List", example = "[1,2,3,4]")
    private List<Integer> crops;
}