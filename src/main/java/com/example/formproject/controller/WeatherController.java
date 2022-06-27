package com.example.formproject.controller;

import com.example.formproject.dto.response.WeatherResponse;
import com.example.formproject.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
//기온 / 날씨 / 강수량 / 습도 / 바람 (하루기준)
/*{
    minTamp : 5.5 (최저기온)
        maxTamp : 20.4 (최고 기온)
    sumRn : 2 (일 강수량)
    avgWs : 2 (평균 풍속)
    avgRhm: 3 (평균 상대 습도)
    weather : “” (날씨)
}*/

    @GetMapping("/weather")
    public WeatherResponse getWeather() throws IOException, ParseException {
        return weatherService.getWeather();
    }
}
