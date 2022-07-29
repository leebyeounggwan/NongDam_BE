package com.example.formproject.service;


import com.example.formproject.annotation.UseCache;
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
        WeatherResponse weatherResponse = new WeatherResponse();

        String address = (!memberdetail.getMember().getAddress().isEmpty()) ? memberdetail.getMember().getAddress() : "서울 송파구 양재대로 932";
        String[] coords = geoService.getGeoPoint(address);
        StringBuilder apiURL = new StringBuilder("https://api.openweathermap.org/data/2.5/onecall?lat=" + coords[1] + "&lon=" + coords[0] + "&appid=1393bfc76e8aafc98311d5fedf3f59bf&units=metric&lang=kr");

        // API 호출
        JSONObject obj = openApiService.ApiCall(apiURL);

        // 현재 기온을 위한 데이터를 파싱
        currentTempParse(obj, address, weatherResponse);

        // 시간별 기온을 위한 데이터 파싱
        hourlyTempParse(obj, weatherResponse);

        // 일별 기온을 위한 데이터 파싱
        dailyTempParse(obj, weatherResponse);

        return weatherResponse;
    }


    public void currentTempParse(JSONObject obj, String address, WeatherResponse weatherResponse) {
        JSONObject parse_response = (JSONObject) obj.get("current");

        JSONObject snow = (JSONObject) parse_response.get("snow");
        if(snow == null){
            weatherResponse.setSn("0");
        } else {
            weatherResponse.setSn(snow.get("1h").toString());
        }
        JSONObject rain = (JSONObject) parse_response.get("rain");
        if(rain == null){
            weatherResponse.setRn("0");
        } else {
            weatherResponse.setRn(rain.get("1h").toString());
        }

        JSONArray parse_weather = (JSONArray) parse_response.get("weather");
        JSONObject value = (JSONObject) parse_weather.get(0);
        String weather = (value.get("description").toString().equals("약간의 구름이 낀 하늘")) ? "구름이 낀 하늘" : value.get("description").toString();
        String icon = value.get("icon").toString();
        weatherResponse.setTemp(parse_response.get("temp").toString().split("\\.")[0]);
        weatherResponse.setWs(String.format("%.1f", ((double) parse_response.get("wind_speed"))));
        weatherResponse.setRhm(parse_response.get("humidity").toString());
        weatherResponse.setWeather(weather);
        weatherResponse.setIconURL("http://idontcare.shop/static/weathericon/"+icon+".png");

        String[] strAddr = address.split(" ");
        weatherResponse.setAddress(strAddr[0]+" "+strAddr[1]);
        String dewPoint = String.format("%.1f", ((double) parse_response.get("dew_point")));
        weatherResponse.setDewPoint(dewPoint);
    }

    public void hourlyTempParse (JSONObject obj, WeatherResponse weatherResponse) {
        JSONArray hourlyArr = (JSONArray) obj.get("hourly");

        List<Long> hTimeList = new ArrayList<>();
        List<String> hTempList = new ArrayList<>();
        List<String> hPopList = new ArrayList<>();
        WeatherDto hour = new WeatherDto();

        for(int i=1; i<17; i+=3) {
            JSONObject hourObj = (JSONObject)hourlyArr.get(i);
            Long time = (Long) hourObj.get("dt");
            hTimeList.add(time);
            hTempList.add(hourObj.get("temp").toString().split("\\.")[0]);

            if (hourObj.get("pop").toString().equals("0")) {
                hPopList.add("0");
            } else if (hourObj.get("pop").toString().equals("1")) {
                hPopList.add("100");
            }
            else {
                double dPop = (double) hourObj.get("pop") * 100;
                int iPop = (int) dPop;
                hPopList.add(Integer.toString(iPop));
            }
            hour.setTime(hTimeList);
            hour.setTemp(hTempList);
            hour.setPop(hPopList);
        }

        weatherResponse.setHour(hour);
    }

    public void dailyTempParse (JSONObject obj, WeatherResponse weatherResponse) {
        JSONArray dailyArr = (JSONArray) obj.get("daily");

        List<Long> dTimeList = new ArrayList<>();
        List<String> dTempList = new ArrayList<>();
        List<String> dPopList = new ArrayList<>();
        WeatherDto day = new WeatherDto();
        for(int i=1; i<7; i++) {
            JSONObject dayObj = (JSONObject) dailyArr.get(i);
            Long time = (Long) dayObj.get("dt");
            dTimeList.add(time);

            JSONObject dayTemp = (JSONObject) dayObj.get("temp");
            dTempList.add(dayTemp.get("day").toString().split("\\.")[0]);
            if(dayObj.get("pop").toString().equals("0")) {
                dPopList.add("0");
            } else if (dayObj.get("pop").toString().equals("1")) {
                dPopList.add("100");
            } else {
                double dPop = (double) dayObj.get("pop") * 100;
                int iPop = (int) dPop;
                dPopList.add(Integer.toString(iPop));
            }
            day.setTime(dTimeList);
            day.setTemp(dTempList);
            day.setPop(dPopList);
        }

        weatherResponse.setDay(day);
    }
}
