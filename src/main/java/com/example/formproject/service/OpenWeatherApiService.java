package com.example.formproject.service;

import com.example.formproject.dto.response.DailyDto;
import com.example.formproject.dto.response.HourlyDto;
import com.example.formproject.dto.response.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OpenWeatherApiService {
    private final GeoService geoService;
//    public static void main(String[] args) throws IOException, ParseException {
    public WeatherResponse getWeather() throws IOException, ParseException {
        String[] coords = geoService.getGeoPoint("서울시 강서구 화곡로 300");
        String lat = coords[1];
        String lon = coords[0];
//        StringBuilder urlBuilder = new StringBuilder("http://api.openweathermap.org/data/2.5/weather?q=seoul&appid=1393bfc76e8aafc98311d5fedf3f59bf&units=metric&lang=kr"); /*URL*/
        StringBuilder urlBuilder = new StringBuilder("https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&appid=1393bfc76e8aafc98311d5fedf3f59bf&units=metric&lang=kr"); /*URL*/


        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;

        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String result = sb.toString();

        // Json parser를 만들어 만들어진 문자열 데이터를 객체화
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(result);

        // 현재 기온을 위한 데이터를 파싱
        JSONObject parse_response = (JSONObject) obj.get("current");
        WeatherResponse weatherResponse = new WeatherResponse();

        JSONObject snow = (JSONObject) parse_response.get("snow");
        if(snow == null){
            weatherResponse.setSn("null");
        } else {
            weatherResponse.setSn(snow.get("1h").toString());
        }
        JSONObject rain = (JSONObject) parse_response.get("rain");
        if(rain == null){
            weatherResponse.setRn("null");
        } else {
            weatherResponse.setRn(rain.get("1h").toString());
        }
        weatherResponse.setTemp(parse_response.get("temp").toString());
        weatherResponse.setWs(parse_response.get("wind_speed").toString());
        weatherResponse.setRhm(parse_response.get("humidity").toString());
        JSONArray parse_weather = (JSONArray) parse_response.get("weather");
        JSONObject value = (JSONObject) parse_weather.get(0);
        weatherResponse.setWeather(value.get("main").toString());


        // 시간별 기온을 위한 데이터 파싱
        JSONArray hourlyArr = (JSONArray) obj.get("hourly");

        List<HourlyDto> hourList = new ArrayList<>();
        for(int i=1; i<17; i+=3) {
            HourlyDto hour = new HourlyDto();
            JSONObject hourObj = (JSONObject)hourlyArr.get(i);
            String time = hourObj.get("dt").toString();

            hour.setTime(getTimestampToDate(time));
            hour.setTemp(hourObj.get("temp").toString());
            hour.setPop(hourObj.get("pop").toString());

            hourList.add(hour);
        }
        weatherResponse.setHour(hourList);

        // 일별 기온을 위한 데이터 파싱
        JSONArray dailyArr = (JSONArray) obj.get("daily");

        List<DailyDto> dayList = new ArrayList<>();
        for(int i=1; i<7; i++) {
            DailyDto day = new DailyDto();
            JSONObject dayObj = (JSONObject) dailyArr.get(i);
            String time = dayObj.get("dt").toString();

            day.setDay(getTimestampToDate(time));
            JSONObject dayTemp = (JSONObject) dayObj.get("temp");
            day.setTemp(dayTemp.get("day").toString());
            day.setPop(dayObj.get("pop").toString());

            dayList.add(day);
        }
        weatherResponse.setDay(dayList);

        return weatherResponse;
    }



    // UMC -> timeStamp 변환
    public static String getTimestampToDate(String timestampStr){
         long timestamp = Long.parseLong(timestampStr);
         Date date = new java.util.Date(timestamp*1000L);
         SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
         String formattedDate = sdf.format(date);
         return formattedDate;
    }

}
