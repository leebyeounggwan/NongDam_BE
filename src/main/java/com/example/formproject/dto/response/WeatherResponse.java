package com.example.formproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class WeatherResponse {


    // 기온
    @Schema(type = "String",example = "25")
    private String temp;
    //강수량
    @Schema(type = "String",example = "2")
    private String rn;
    //적설량
    @Schema(type = "String",example = "0")
    private String sn;
    //풍속
    @Schema(type = "String",example = "2.7")
    private String ws;
    //습도
    @Schema(type = "String",example = "87")
    private String rhm;
    //날씨
    @Schema(type = "String",example = "흐림")
    private String weather;
    //아이콘 URL
    @Schema(type = "String",example = "http://idontcare.shop/static/weathericon/01d.png")
    private String iconURL;
    //지역
    @Schema(type = "String",example = "서울시 용산구")
    private String address;
    //이슬점
    @Schema(type = "String",example = "22.3")
    private String dewPoint;
    //시간별 날씨
    private WeatherDto hour;
    //주간 날씨
    private WeatherDto day;

}

    /*{
    기온 / 날씨 / 강수량 / 습도 / 바람 (하루기준)
        minTamp : 5.5 (최저기온)
            maxTamp : 20.4 (최고 기온)
        sumRn : 2 (일 강수량)
        avgWs : 2 (평균 풍속)
        avgRhm: 3 (평균 상대 습도)
        weather : “” (날씨)
    }*/