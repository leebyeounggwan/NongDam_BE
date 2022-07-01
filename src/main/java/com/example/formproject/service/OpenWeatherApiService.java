package com.example.formproject.service;

import com.example.formproject.annotation.UseCache;
import com.example.formproject.dto.response.DailyWeatherDto;
import com.example.formproject.dto.response.HourlyWeatherDto;
import com.example.formproject.dto.response.WeatherResponse;
import com.example.formproject.security.MemberDetail;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OpenWeatherApiService {
    private final GeoService geoService;
    @UseCache(ttlHour = 2L,cacheKey = "cacheKey")
    public WeatherResponse getWeather(MemberDetail memberdetail, int cacheKey) throws IOException, ParseException {
        String address;
        if (memberdetail.getMember().getCountryCode() == 0) {
            address = "서울시 강서구 화곡로 302";
        } else {
            address = "서울시 강서구 화곡로 302";
        }

        String[] coords = geoService.getGeoPoint(address);
        String lat = coords[1];
        String lon = coords[0];

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
        weatherResponse.setTemp(parse_response.get("temp").toString());
        weatherResponse.setWs(parse_response.get("wind_speed").toString());
        weatherResponse.setRhm(parse_response.get("humidity").toString());
        JSONArray parse_weather = (JSONArray) parse_response.get("weather");
        JSONObject value = (JSONObject) parse_weather.get(0);
        weatherResponse.setWeather(value.get("description").toString());
        String icon = value.get("icon").toString();
        weatherResponse.setIconURL("http://openweathermap.org/img/wn/"+icon+"@2x.png");

        String[] strAddr = address.split(" ");

        weatherResponse.setAddress(strAddr[0]+" "+strAddr[1]);
        weatherResponse.setDewPoint(parse_response.get("dew_point").toString());
//        (([가-힣]+(시|도)|[서울]|[인천]|[대구]|[광주]|[부산]|[울산])( |)[가-힣]+(시|군|구))

        // 시간별 기온을 위한 데이터 파싱
        JSONArray hourlyArr = (JSONArray) obj.get("hourly");

        List<HourlyWeatherDto> hourList = new ArrayList<>();
        List<String> hTimeList = new ArrayList<>();
        List<String> hTempList = new ArrayList<>();
        List<String> hPopList = new ArrayList<>();
        HourlyWeatherDto hour = new HourlyWeatherDto();
        for(int i=1; i<17; i+=3) {
            JSONObject hourObj = (JSONObject)hourlyArr.get(i);
            String time = hourObj.get("dt").toString();
            hTimeList.add(getTimestampToDate(time));
            hTempList.add(hourObj.get("temp").toString());

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
        hourList.add(hour);
        weatherResponse.setHour(hourList);

        // 일별 기온을 위한 데이터 파싱
        JSONArray dailyArr = (JSONArray) obj.get("daily");

        List<DailyWeatherDto> dayList = new ArrayList<>();
        List<String> dTimeList = new ArrayList<>();
        List<String> dTempList = new ArrayList<>();
        List<String> dPopList = new ArrayList<>();
        DailyWeatherDto day = new DailyWeatherDto();
        for(int i=1; i<7; i++) {
            JSONObject dayObj = (JSONObject) dailyArr.get(i);
            String time = dayObj.get("dt").toString();
            dTimeList.add(getTimestampToDate(time));

            JSONObject dayTemp = (JSONObject) dayObj.get("temp");
            dTempList.add(dayTemp.get("day").toString());
            if(dayObj.get("pop").toString().equals("0")) {
                dPopList.add("0");
            } else if (dayObj.get("pop").toString().equals("1")) {
                dPopList.add("100");
            } else {

                double dPop = (double) dayObj.get("pop") * 100;
                int iPop = (int) dPop;
                dPopList.add(Integer.toString(iPop));
            }
            day.setDay(dTimeList);
            day.setTemp(dTempList);
            day.setPop(dPopList);
        }
        dayList.add(day);
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
