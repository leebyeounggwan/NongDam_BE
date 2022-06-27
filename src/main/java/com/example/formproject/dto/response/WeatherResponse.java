package com.example.formproject.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WeatherResponse {
    //기온 / 날씨 / 강수량 / 습도 / 바람 (하루기준)
    /*{
        minTamp : 5.5 (최저기온)
            maxTamp : 20.4 (최고 기온)
        sumRn : 2 (일 강수량)
        avgWs : 2 (평균 풍속)
        avgRhm: 3 (평균 상대 습도)
        weather : “” (날씨)
    }*/

    private String minTemp;
    private String maxTemp;
    private String sumRn;
    private String avgWs;
    private String avgRhm;
    private String weather;

}
