package com.example.formproject.service;

import com.example.formproject.dto.response.WeatherResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OpenWeatherApiService {
    public static void main(String[] args) throws IOException, ParseException {
//        StringBuilder urlBuilder = new StringBuilder("http://api.openweathermap.org/data/2.5/weather?q=seoul&appid=1393bfc76e8aafc98311d5fedf3f59bf&units=metric&lang=kr"); /*URL*/
        StringBuilder urlBuilder = new StringBuilder("https://api.openweathermap.org/data/2.5/onecall?lat=37.551083&lon=126.8496128&appid=1393bfc76e8aafc98311d5fedf3f59bf&units=metric&lang=kr"); /*URL*/


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
        System.out.println(result);

        // Json parser를 만들어 만들어진 문자열 데이터를 객체화
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(result);

        // response 키를 가지고 데이터를 파싱
        JSONObject parse_response = (JSONObject) obj.get("response");

        // response 로 부터 body 찾기
        JSONObject parse_body = (JSONObject) parse_response.get("body");
        // body 로 부터 items 찾기
        JSONObject parse_items = (JSONObject) parse_body.get("items");

        // items로 부터 itemlist 를 받기
        JSONArray parse_item = (JSONArray) parse_items.get("item");
        JSONObject value = (JSONObject) parse_item.get(0);

        //weatherResponse에 저장
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setMinTemp(value.get("minTa").toString());
        weatherResponse.setMaxTemp(value.get("maxTa").toString());
        weatherResponse.setSumRn(value.get("sumRn").toString());
        weatherResponse.setAvgWs(value.get("avgWs").toString());
        weatherResponse.setAvgRhm(value.get("avgRhm").toString());

//        return weatherResponse;

    }

    public String getTimestampToDate(String timestampStr){
         long timestamp = Long.parseLong(timestampStr);
         Date date = new java.util.Date(timestamp*1000L);
         SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
         String formattedDate = sdf.format(date);
         return formattedDate;
    }



}
