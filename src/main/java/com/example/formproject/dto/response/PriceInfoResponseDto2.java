package com.example.formproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PriceInfoResponseDto2 {
    //당일 시세
    private String dpr1;
    //1일전 시세
    private String dpr2;
    //1주일전 시세
    private String dpr3;
    //2주일전 시세
    private String dpr4;
    //1개월전 시세
    private String dpr5;
    //1년전 시세
    private String dpr6;
}
