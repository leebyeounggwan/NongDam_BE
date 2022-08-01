package com.example.formproject.dto.response;

import lombok.Getter;

@Getter
public class WeatherResponse {

    private final CurrentTempDto currentTempDto;
    //시간별 날씨
    private final WeatherDto hour;
    //주간 날씨
    private final WeatherDto day;

    public WeatherResponse(CurrentTempDto currentTempDto, WeatherDto hourlyTemp, WeatherDto dailyTemp) {
        this.currentTempDto = currentTempDto;
        this.hour = hourlyTemp;
        this.day = dailyTemp;
    }
}

