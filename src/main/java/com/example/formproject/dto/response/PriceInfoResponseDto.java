package com.example.formproject.dto.response;

import com.example.formproject.dto.request.PriceInfoRequestDto2;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PriceInfoResponseDto {
    private String unit;
    private String country;
    private String wholesale;
    private String latestDate;
    private String latestDatePrice;
    private List<String> dateList;
    private List<String> priceList;

    public PriceInfoResponseDto(
            DailyPriceInfoDto dailyPrice,
            MonthlyPriceInfoDto monthlyPriceInfoDto,
            PriceInfoRequestDto2 priceInfoRequestDto2) {
        this.unit = monthlyPriceInfoDto.getUnit();
        this.country = dailyPrice.getCountyname();
        this.wholesale = priceInfoRequestDto2.getProductClsCode();
        this.dateList = monthlyPriceInfoDto.getDateList();
        this.priceList = monthlyPriceInfoDto.getPriceList();

        if(dailyPrice.getPrice() != null) {
            this.latestDate = dailyPrice.getYear();
            this.latestDatePrice = dailyPrice.getPrice();
        } else {
            if (!priceList.isEmpty()) {
                this.latestDate = this.dateList.get(dateList.size()-1);
                this.latestDatePrice = this.priceList.get(priceList.size()-1);
            }
        }
    }
}
//    DailyPriceInfoDto dailyPrice = dailyPrice(priceInfoRequestDto);
//    MonthlyPriceInfoDto monthlyPriceInfoDto = monthlyPrice(priceInfoRequestDto);
