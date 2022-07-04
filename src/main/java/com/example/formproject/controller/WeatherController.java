package com.example.formproject.controller;

import com.example.formproject.dto.response.WeatherResponse;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.GeoService;
import com.example.formproject.service.OpenWeatherApiService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final OpenWeatherApiService weatherService;

    @GetMapping("/weather")
    public WeatherResponse getWeather(@AuthenticationPrincipal MemberDetail memberdetail) throws IOException, ParseException {
//        String cacheKey = memberdetail.getMember().getName()+":"+memberdetail.getMember().getId();
        return weatherService.getWeather(memberdetail,memberdetail.getMember().getId());
    }

}
