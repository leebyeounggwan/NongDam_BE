package com.example.formproject.dto.response;

import com.example.formproject.dto.request.PriceApiRequestVariableDto;
import com.example.formproject.dto.request.PriceInfoRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
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

    public DailyPriceResponseDto(PriceMetaDto meta, JSONObject obj) {

        JSONObject parse_data = (JSONObject) obj.get("data");
        JSONArray parse_item = (JSONArray) parse_data.get("item");
        JSONObject parse_latestDate = new JSONObject();

        parse_latestDate = getLatestPrice(parse_item, parse_latestDate);

        this.crop = meta.getCrop();
        this.type = meta.getType();
        this.unit = meta.getUnit();
        this.country = meta.getCountry();
        this.wholeSale = meta.getWholeSale();
        this.latestDate = parse_latestDate.get("yyyy").toString() + "-" + parse_latestDate.get("regday").toString().replace("/", "-");
        this.latestDatePrice = parse_latestDate.get("price").toString();
    }

    public DailyPriceResponseDto(PriceMetaDto meta) {
        this.crop = meta.getCrop();
        this.type = meta.getType();
        this.unit = meta.getUnit();
        this.country = meta.getCountry();
        this.wholeSale = meta.getWholeSale();
        this.latestDate = "";
        this.latestDatePrice = "";
    }

    private JSONObject getLatestPrice(JSONArray parse_item, JSONObject parse_latestDate) {
        for (int i = 0; i < parse_item.size(); i++) {
            parse_latestDate = (JSONObject) parse_item.get(i);
            if (parse_latestDate.get("countyname").equals("평년")) {
                parse_latestDate = (JSONObject) parse_item.get(i-1);
                break;
            }
        }
        return parse_latestDate;
    }
}

