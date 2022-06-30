package com.example.formproject.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class WeatherResponse {


    // 기온
    private String temp;
    //강수량
    private String rn;
    //적설량
    private String sn;
    //풍속
    private String ws;
    //습도
    private String rhm;
    //날씨
    private String weather;
    //아이콘 URL
    private String iconURL;
    //시간별 날씨
    private List<HourlyWeatherDto> hour;
    //주간 날씨
    private List<DailyWeatherDto> day;

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