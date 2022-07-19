package com.example.formproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceApiRequestVariableDto {
    private String categoryCode;
    private String itemCode;
    private String kindCode;
    private String gradeRank;
    private String countryCode;
    private String nowYear;
    private String startDay;
    private String endDay;
    private String clsCode;

    public PriceApiRequestVariableDto(PriceInfoRequestDto priceInfoRequestDto, List<String> dailyDate) {
        this.categoryCode = priceInfoRequestDto.getCategory() + "";
        this.itemCode = priceInfoRequestDto.getType() + "";
        this.kindCode = priceInfoRequestDto.getKind();
        this.gradeRank = "04";
        this.countryCode = (priceInfoRequestDto.getCountryCode().equals("0")) ? "1101" : priceInfoRequestDto.getCountryCode();
        this.nowYear = priceInfoRequestDto.getYear() + "";
        this.startDay = dailyDate.get(0);
        this.endDay = dailyDate.get(1);
        this.clsCode = (priceInfoRequestDto.getProductClsCode().equals("소매")) ? "01" : "02";
    }

    public PriceApiRequestVariableDto(PriceInfoRequestDto priceInfoRequestDto) {
        this.categoryCode = priceInfoRequestDto.getCategory() + "";
        this.itemCode = priceInfoRequestDto.getType() + "";
        this.kindCode = priceInfoRequestDto.getKind();
        this.gradeRank = "1";
        this.countryCode = (priceInfoRequestDto.getCountryCode().equals("0")) ? "1101" : priceInfoRequestDto.getCountryCode();
        this.nowYear = priceInfoRequestDto.getYear() + "";
        this.startDay = "";
        this.endDay = "";
    }
}
