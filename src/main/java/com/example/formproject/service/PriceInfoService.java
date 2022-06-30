package com.example.formproject.service;

import com.example.formproject.dto.response.HourlyWeatherDto;
import com.example.formproject.dto.response.PriceInfoResponseDto;
import com.example.formproject.dto.response.PriceInfoResponseDto2;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class PriceInfoService {

    //e038dee1-a1d5-426d-ba4c-6bcd8c7100cb

//    public static void main(String[] args) throws IOException, ParseException {
//
//        String p_startday = "2022-06-01";
//        String p_endday = "2022-06-01";
//        String p_itemcategorycode = "200";
//        String p_itemcode = "212";
//        String p_kindcode = "00";
//        String p_productrankcode = "04";
//        String p_countrycode = "1101";
//
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
//        // Json parser를 만들어 만들어진 문자열 데이터를 객체화
//        JSONParser parser = new JSONParser();
//        JSONObject obj = (JSONObject) parser.parse(result);
//        JSONObject parse_data = (JSONObject) obj.get("data");
//        JSONArray parse_item = (JSONArray) parse_data.get("item");
//        List<PriceInfoResponseDto> priceInfoResponseDtoList = new ArrayList<>();
//
//        for (int i = (parse_item.size()/3)*2; i-(parse_item.size()/3)*2 < (parse_item.size())/3; i++) {
//            System.out.println("====");
//            PriceInfoResponseDto priceInfoResponseDto = new PriceInfoResponseDto();
//
//            JSONObject priceObj = (JSONObject) parse_item.get(i);
//
//            priceInfoResponseDto.setDate(priceObj.get("regday").toString());
//            priceInfoResponseDto.setPrice(priceObj.get("price").toString());
//
//            priceInfoResponseDtoList.add(priceInfoResponseDto);
//            System.out.println(priceInfoResponseDto.getDate());
//            System.out.println(priceInfoResponseDto.getPrice());
//        }
//
//    }
    public static void main(String[] args) throws IOException, ParseException {
//    public void getPr() throws IOException, ParseException {

//        String p_startday = "2022-06-01";
//        String p_endday = "2022-06-01";
//        String p_itemcategorycode = "200";
//        String p_itemcode = "212";
//        String p_kindcode = "00";
//        String p_productrankcode = "04";
//        String p_countrycode = "1101";
//        StringBuilder urlBuilder = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=periodProductList&p_productclscode=02&p_startday="+p_startday+"&p_endday="+p_endday+"&p_itemcategorycode="+p_itemcategorycode+"&p_itemcode="+p_itemcode+"&p_kindcode="+p_kindcode+"&p_productrankcode="+p_productrankcode+"&p_countrycode="+p_countrycode+"&p_convert_kg_yn=Y&p_cert_key=e038dee1-a1d5-426d-ba4c-6bcd8c7100cb&p_cert_id=lbk0622&p_returntype=json"); /*URL*/
//        http://www.kamis.or.kr/service/price/xml.do?action=dailyPriceByCategoryList&p_product_cls_code=02&p_country_code=1101&p_regday=2015-10-01&p_convert_kg_yn=N&p_item_category_code=200&p_cert_key=111&p_cert_id=222&p_returntype=xml
        StringBuilder urlBuilder = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=dailyPriceByCategoryList&p_product_cls_code=02&p_country_code=1101&p_regday=2022-06-07&p_convert_kg_yn=Y&p_item_category_code=200&p_cert_key=e038dee1-a1d5-426d-ba4c-6bcd8c7100cb&p_cert_id=lbk0622&p_returntype=json"); /*URL*/

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
//        System.out.println(result);
        // Json parser를 만들어 만들어진 문자열 데이터를 객체화
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(result);
        JSONObject parse_data = (JSONObject) obj.get("data");
        JSONArray parse_item = (JSONArray) parse_data.get("item");

        for (int i = 0; i < parse_item.size(); i++) {
            JSONObject a = (JSONObject) parse_item.get(i);
            if(a.get("item_code").toString().equals("212")) {
                if(a.get("rank_code").toString().equals("04")){
                    PriceInfoResponseDto2 priceInfoResponseDto2 = new PriceInfoResponseDto2();
                    System.out.println(a);
                    priceInfoResponseDto2.setDpr1(a.get("dpr1").toString());
                    priceInfoResponseDto2.setDpr2(a.get("dpr2").toString());
                    priceInfoResponseDto2.setDpr3(a.get("dpr3").toString());
                    priceInfoResponseDto2.setDpr4(a.get("dpr4").toString());
                    priceInfoResponseDto2.setDpr5(a.get("dpr5").toString());
                    priceInfoResponseDto2.setDpr6(a.get("dpr6").toString());
                    System.out.println("당일 가격 : "+priceInfoResponseDto2.getDpr1());
                    System.out.println("1일전 가격 : "+priceInfoResponseDto2.getDpr2());
                    System.out.println("1주일전 가격 : "+priceInfoResponseDto2.getDpr3());
                    System.out.println("2주전 가격 : "+priceInfoResponseDto2.getDpr4());
                    System.out.println("1달전 가격 : "+priceInfoResponseDto2.getDpr5());
                    System.out.println("1년전 가격 : "+priceInfoResponseDto2.getDpr6());
                }
            }
        }
    }


}
