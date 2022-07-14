package com.example.formproject.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceRequestDto {
    @Schema(type = "int",example = "21")
    private int cropId;
    @Schema(type = "String",example = "소매")
    private String productClsCode;
    @Schema(type = "String",example = "상품")
    private String gradeRank;
    @Schema(type = "String",example = "month")
    private String data;
}
