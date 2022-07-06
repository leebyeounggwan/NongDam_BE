package com.example.formproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PriceInfoResponseDto {
    private String productClsCode;
    private String itemCode;
    private String kindCode;
    private String rankCode;
    private String unit;
    private String market;
    private int todayPrice;
    private List<String> dateList;
    private List<String> priceList;

}
