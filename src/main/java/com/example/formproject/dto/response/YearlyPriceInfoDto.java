package com.example.formproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class YearlyPriceInfoDto {
    private List<String> dateList;
    private List<String> priceList;

    public YearlyPriceInfoDto(List<String> dateList, List<String> priceList) {
        this.dateList = dateList;
        this.priceList = priceList;
    }
}
