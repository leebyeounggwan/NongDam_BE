package com.example.formproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PriceInfoDto {
    private String crop;
    private String type;
    private String unit;
    private String country;
    private String wholeSale;
    private List<String> dateList;
    private List<String> priceList;
}
