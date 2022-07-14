package com.example.formproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyPriceResponseDto {
    @Schema(type = "String",example = "쌀")
    private String crop;
    @Schema(type = "String",example = "흑미")
    private String type;
    @Schema(type = "String",example = "kg")
    private String unit;
    @Schema(type = "String",example = "서울")
    private String country;
    @Schema(type = "String",example = "소매")
    private String wholeSale;
    @Schema(type = "String",example = "2022-07-13")
    private String latestDate;
    @Schema(type = "String",example = "1,500")
    private String latestDatePrice;
}