package com.example.formproject.dto.response;
import com.example.formproject.dto.request.PriceInfoRequestDto;
import com.example.formproject.enums.CountryCode;
import com.example.formproject.enums.CropTypeCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public class PriceInfoDto {
    @Schema(type = "String",example = "쌀")
    private String crop;
    @Schema(type = "String",example = "백미")
    private String type;
    @Schema(type = "String",example = "kg")
    private String unit;
    @Schema(type = "String",example = "서울")
    private String country;
    @Schema(type = "String",example = "소매")
    private String wholeSale;
    @Schema(type = "List<String>",example = "[2021-07,2021-09,2021-11,2022-01,2022-03,2022-05,2022-07]")
    private List<String> dateList;
    @Schema(type = "List<String>",example = "[1,500,1,600,1,400,1,500,1,700,1,700,1,550]")
    private List<String> priceList;

    public PriceInfoDto(PriceInfoRequestDto priceInfoRequestDto, String countyCode, String clsCode, String[] stringa, String unit, List<String> dateList, List<String> priceList) {
        this.crop = stringa[2];
        this.type = priceInfoRequestDto.getName();
        this.unit = unit;
        this.country = CountryCode.findByCountryCode(Integer.parseInt(countyCode)).toString();
        this.wholeSale = clsCode;
        this.dateList = dateList;
        this.priceList = priceList;
    }
    public PriceInfoDto(PriceInfoRequestDto priceInfoRequestDto, String itemCode, String countyCode, int i) {
        List<String> list = Collections.emptyList();
        this.crop = CropTypeCode.findByCode(Integer.parseInt(itemCode)).toString();
        this.type = priceInfoRequestDto.getName();
        this.unit = "kg";
        this.country = CountryCode.findByCountryCode(Integer.parseInt(countyCode)).toString();
        this.wholeSale = (i==0) ? "도매" : "소매";
        this.dateList = list;
        this.priceList = list;
    }
}
