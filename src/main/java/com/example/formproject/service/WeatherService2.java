//package com.example.formproject.service;
//
//import com.example.formproject.dto.response.WeatherResponse;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.io.BufferedReader;
//import java.io.IOException;
//
//public class WeatherService2 {
//    public static void main(String[] args) throws IOException, ParseException {
////    public WeatherResponse getWeather() throws IOException, ParseException {
//        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
//        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + "dTXHhacoHDFAA3OmzHaV0A2FCm1j3nBbvfh1w4BQ3tEBfQHxgAWveIvTzBAWIGiuo1iayK9cxt3t6LJDs571Zg%3D%3D"); /*Service Key*/
//        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
//        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("30", "UTF-8")); /*한 페이지 결과 수*/
//        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
//        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode("20220627", "UTF-8")); /*‘21년 6월 28일 발표*/
//        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0500", "UTF-8")); /*06시 발표(정시단위) */
//        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("55", "UTF-8")); /*예보지점의 X 좌표값*/
//        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("127", "UTF-8")); /*예보지점의 Y 좌표값*/
//        URL url = new URL(urlBuilder.toString());
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Content-type", "application/json");
//        System.out.println("Response code: " + conn.getResponseCode());
//        BufferedReader rd;
//        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        } else {
//            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//        }
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = rd.readLine()) != null) {
//            sb.append(line);
//        }
//        rd.close();
//        conn.disconnect();
//        System.out.println(sb.toString());
//        String result = sb.toString();
//
//        // Json parser를 만들어 만들어진 문자열 데이터를 객체화
//        JSONParser parser = new JSONParser();
//        JSONObject obj = (JSONObject) parser.parse(result);
//
//        // response 키를 가지고 데이터를 파싱
//        JSONObject parse_response = (JSONObject) obj.get("response");
//
//        // response 로 부터 body 찾기
//        JSONObject parse_body = (JSONObject) parse_response.get("body");
//        // body 로 부터 items 찾기
//        JSONObject parse_items = (JSONObject) parse_body.get("items");
//
//        // items로 부터 itemlist 를 받기
//        JSONArray parse_item = (JSONArray) parse_items.get("item");
//        JSONObject value = (JSONObject) parse_item.get(0);
//
//        //weatherResponse에 저장
//        WeatherResponse weatherResponse = new WeatherResponse();
//        weatherResponse.setMinTemp(value.get("TMN").toString());
//        weatherResponse.setMaxTemp(value.get("TMX").toString());
//        weatherResponse.setSumRn(value.get("PCP").toString());
//        weatherResponse.setAvgWs(value.get("WSK").toString());
//        weatherResponse.setAvgRhm(value.get("REH").toString());
//        weatherResponse.setWeather(value.get("SKY").toString());
//        System.out.println("weatherResponse = " + weatherResponse.getWeather());
////        return weatherResponse;
//
//
//    }
//}