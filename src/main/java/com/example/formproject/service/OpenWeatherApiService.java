package com.example.formproject.service;


import com.example.formproject.annotation.UseCache;
import com.example.formproject.dto.response.CurrentTempDto;
import com.example.formproject.dto.response.WeatherDto;
import com.example.formproject.dto.response.WeatherResponse;
import com.example.formproject.security.MemberDetail;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class OpenWeatherApiService {
    private final OpenApiService openApiService;
    private final GeoService geoService;
    @UseCache(ttl = 0L,cacheKey = "cacheKey",unit = TimeUnit.MINUTES,timeData = false)
    public WeatherResponse getWeather(MemberDetail memberdetail, int cacheKey) throws Exception {

        String address = (!memberdetail.getMember().getAddress().isEmpty()) ? memberdetail.getMember().getAddress() : "서울 송파구 양재대로 932";
        String[] coords = geoService.getGeoPoint(address);
        StringBuilder apiURL = new StringBuilder("https://api.openweathermap.org/data/2.5/onecall?lat=" + coords[1] + "&lon=" + coords[0] + "&appid=1393bfc76e8aafc98311d5fedf3f59bf&units=metric&lang=kr");

        JSONObject obj = openApiService.ApiCall(apiURL);

        // 현재 기온
        CurrentTempDto currentTempDto = currentTempParse(obj, address);
        // 시간별 기온
        WeatherDto hourlyTemp = setTempList(obj, "hourly");
        // 주간 기온
        WeatherDto dailyTemp = setTempList(obj, "daily");

        return new WeatherResponse(currentTempDto, hourlyTemp, dailyTemp);
    }


    public CurrentTempDto currentTempParse(JSONObject obj, String address) {
        
        return new CurrentTempDto(obj, address);
    }

    public WeatherDto setTempList (JSONObject obj, String select) {

        return new WeatherDto(obj, select);
    }

}
