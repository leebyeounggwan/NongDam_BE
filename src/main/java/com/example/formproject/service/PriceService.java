//package com.example.formproject.service;
//
//import com.example.formproject.dto.response.WeatherResponse;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//import org.springframework.stereotype.Service;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//@Service
//public class PriceService {
//
//    //e038dee1-a1d5-426d-ba4c-6bcd8c7100cb
//
//    public static void main(String[] args) throws IOException, ParseException {
//        String p_startday = "2022-06-01";
//        String p_endday = "2022-06-10";
//        String p_itemcategorycode = "200";
//        String p_itemcode = "212";
//        String p_kindcode = "00";
//        String p_productrankcode = "04";
//        String p_countrycode = "1101";
////        p_itemcategorycode=200&p_itemcode=212&p_kindcode=00&p_productrankcode=04&p_countrycode=1101
//        StringBuilder urlBuilder = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=periodProductList&p_productclscode=02&p_startday="+p_startday+"&p_endday="+p_endday+"&p_itemcategorycode="+p_itemcategorycode+"&p_itemcode="+p_itemcode+"&p_kindcode="+p_kindcode+"&p_productrankcode="+p_productrankcode+"&p_countrycode="+p_countrycode+"&p_convert_kg_yn=Y&p_cert_key=e038dee1-a1d5-426d-ba4c-6bcd8c7100cb&p_cert_id=lbk0622&p_returntype=json"); /*URL*/
//
//        URL url = new URL(urlBuilder.toString());
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Content-type", "application/json");
//
//        BufferedReader rd;
//
//        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = rd.readLine()) != null) {
//            sb.append(line);
//        }
//        rd.close();
//        conn.disconnect();
//        String result = sb.toString();
//        System.out.println(result);
//
//
//
//        // Json parser를 만들어 만들어진 문자열 데이터를 객체화
//        JSONParser parser = new JSONParser();
//        JSONObject obj = (JSONObject) parser.parse(result);
//        JSONObject a = (JSONObject) obj.get("data");
//        JSONArray b = (JSONArray) a.get("item");
//
//        System.out.println(b.size());
//
////        for (Object o : a) {
////            System.out.println(o);
////        }
////        System.out.println(a);
////
////        // response 키를 가지고 데이터를 파싱
////        JSONObject parse_response = (JSONObject) obj.get("response");
////
////        // response 로 부터 body 찾기
////        JSONObject parse_body = (JSONObject) parse_response.get("body");
////        // body 로 부터 items 찾기
////        JSONObject parse_items = (JSONObject) parse_body.get("items");
////
////        // items로 부터 itemlist 를 받기
////        JSONArray parse_item = (JSONArray) parse_items.get("item");
////        JSONObject value = (JSONObject) parse_item.get(0);
////
////        //weatherResponse에 저장
////        WeatherResponse weatherResponse = new WeatherResponse();
////        weatherResponse.setMinTemp(value.get("minTa").toString());
////        weatherResponse.setMaxTemp(value.get("maxTa").toString());
////        weatherResponse.setSumRn(value.get("sumRn").toString());
////        weatherResponse.setAvgWs(value.get("avgWs").toString());
////        weatherResponse.setAvgRhm(value.get("avgRhm").toString());
//
////        return weatherResponse;
//
//
//
//    }
//
//}
