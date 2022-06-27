package com.example.formproject.service;


import com.example.formproject.dto.response.WeatherResponse;
import org.apache.catalina.mbeans.SparseUserDatabaseMBean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
@Service
public class WeatherService {
        public WeatherResponse getWeather() throws IOException, ParseException {
        //기온 / 날씨 / 강수량 / 습도 / 바람 (하루기준)
        /*{
            minTamp : 5.5 (최저기온)
            maxTamp : 20.4 (최고 기온)
            sumRn : 2 (일 강수량)
            avgWs : 2 (평균 풍속)
            avgRhm: 3 (평균 상대 습도)
            weather : “” (날씨)
        }*/
        String address;


        // 현재 날짜 구하기
        LocalDate now = LocalDate.now();
        System.out.println("now = " + now);
        // 포맷 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // 포맷 적용
        String date = now.format(formatter);
        System.out.println("date = " + date);
        int dateT = 20220627;

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/AsosDalyInfoService/getWthrDataList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + "dTXHhacoHDFAA3OmzHaV0A2FCm1j3nBbvfh1w4BQ3tEBfQHxgAWveIvTzBAWIGiuo1iayK9cxt3t6LJDs571Zg%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호 Default : 1*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수 Default : 10*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default : XML*/
        urlBuilder.append("&" + URLEncoder.encode("dataCd","UTF-8") + "=" + URLEncoder.encode("ASOS", "UTF-8")); /*자료 분류 코드(ASOS)*/
        urlBuilder.append("&" + URLEncoder.encode("dateCd","UTF-8") + "=" + URLEncoder.encode("DAY", "UTF-8")); /*날짜 분류 코드(DAY)*/
        urlBuilder.append("&" + URLEncoder.encode("startDt","UTF-8") + "=" + dateT); /*조회 기간 시작일(YYYYMMDD)*/
        urlBuilder.append("&" + URLEncoder.encode("endDt","UTF-8") + "=" + dateT); /*조회 기간 종료일(YYYYMMDD) (전일(D-1)까지 제공)*/
//      urlBuilder.append("&" + URLEncoder.encode("startDt","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); /*조회 기간 시작일(YYYYMMDD)*/
//      urlBuilder.append("&" + URLEncoder.encode("endDt","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); /*조회 기간 종료일(YYYYMMDD) (전일(D-1)까지 제공)*/
        urlBuilder.append("&" + URLEncoder.encode("stnIds","UTF-8") + "=" + URLEncoder.encode("108", "UTF-8")); /*종관기상관측 지점 번호 (활용가이드 하단 첨부 참조)*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
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

        return weatherResponse;
    }
        //토큰으로부터 주소값 가져오기
//        public void getAddress(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//            User user = userDetails.getUser();
//            String userAddress = user.getAddress();
//            return result;
//        }


}




//    // Json parser를 만들어 만들어진 문자열 데이터를 객체화
//    JSONParser parser = new JSONParser();
//
//    JSONObject obj = (JSONObject) parser.parse(result);
//    // response 키를 가지고 데이터를 파싱
//    JSONObject parse_response = (JSONObject) obj.get("response");
//    // response 로 부터 body 찾기
//    JSONObject parse_body = (JSONObject) parse_response.get("body");
//    // body 로 부터 items 찾기
//    JSONObject parse_items = (JSONObject) parse_body.get("items");
//
//    // items로 부터 itemlist 를 받기
//    JSONArray parse_item = (JSONArray) parse_items.get("item");
//    String category;
//    JSONObject weather; // parse_item은 배열형태이기 때문에 하나씩 데이터를 하나씩 가져올때 사용
//    // 카테고리와 값만 받아오기
//    String day="";
//    String time="";
//        for(int i = 0 ; i < parse_item.size(); i++) {
//        weather = (JSONObject) parse_item.get(i);
//        System.out.println(weather.toString());
//        Object fcstValue = weather.get("fcstValue");
//        Object fcstDate = weather.get("fcstDate");
//        Object fcstTime = weather.get("fcstTime");
//        //double형으로 받고싶으면 아래내용 주석 해제
//        //double fcstValue = Double.parseDouble(weather.get("fcstValue").toString());
//        category = (String)weather.get("category");
//        // 출력
//        if(!day.equals(fcstDate.toString())) {
//        day=fcstDate.toString();
//        }
//        if(!time.equals(fcstTime.toString())) {
//        time=fcstTime.toString();
//        System.out.println(day+"  "+time);
//        }
//        System.out.print("\tcategory : "+ category);
//        System.out.print(", fcst_Value : "+ fcstValue);
//        System.out.print(", fcstDate : "+ fcstDate);
//        System.out.println(", fcstTime : "+ fcstTime);
//        }

