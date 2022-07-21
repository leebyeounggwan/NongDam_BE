package com.example.formproject.dto.response;

import com.example.formproject.dto.request.PriceInfoRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import com.example.formproject.enums.CountryCode;
import com.example.formproject.enums.CropTypeCode;


@Getter
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

    public DailyPriceResponseDto(PriceInfoRequestDto priceInfoRequestDto, JSONObject parse_latestDate, String unit) {
        this.crop = parse_latestDate.get("itemname").toString();
        this.type = priceInfoRequestDto.getName();
        this.unit = unit;
        this.country = parse_latestDate.get("countyname").toString();
        this.wholeSale = priceInfoRequestDto.getProductClsCode();
        this.latestDate = parse_latestDate.get("yyyy").toString() + "-" + parse_latestDate.get("regday").toString().replace("/", "-");
        this.latestDatePrice = parse_latestDate.get("price").toString();
    }

    public DailyPriceResponseDto(PriceInfoRequestDto priceInfoRequestDto, String p_itemcode, String p_countrycode) {
        this.crop = CropTypeCode.findByCode(Integer.parseInt(p_itemcode)).toString();
        this.type = priceInfoRequestDto.getName();
        this.unit = "kg";
        this.country = CountryCode.findByCountryCode(Integer.parseInt(p_countrycode)).toString();
        this.wholeSale = priceInfoRequestDto.getProductClsCode();
        this.latestDate = "";
        this.latestDatePrice = "";
    }
}

