package com.example.formproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MonthlyPriceInfoDto {
    private String unit;
    private List<String> dateList;
    private List<String> priceList;
}
