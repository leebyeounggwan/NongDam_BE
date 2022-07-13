package com.example.formproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyPriceInfoDto {
    private String crop;
    private String type;
    private String unit;
    private String country;
    private String wholeSale;
    private String latestDate;
    private String latestDatePrice;
}