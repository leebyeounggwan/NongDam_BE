package com.example.formproject.service;

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

    //apikey = e038dee1-a1d5-426d-ba4c-6bcd8c7100cb

    public List<PriceInfoResponseDto> dailyPrice() throws IOException, ParseException {
        String p_startday = "2022-06-01";
        String p_endday = "2022-06-01";
        String p_itemcategorycode = "200";
        String p_itemcode = "212";
        String p_kindcode = "00";
        String p_productrankcode = "04";
        String p_countrycode = "1101";

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=periodProductList&p_productclscode=02&p_startday="+p_startday+"&p_endday="+p_endday+"&p_itemcategorycode="+p_itemcategorycode+"&p_itemcode="+p_itemcode+"&p_kindcode="+p_kindcode+"&p_productrankcode="+p_productrankcode+"&p_countrycode="+p_countrycode+"&p_convert_kg_yn=Y&p_cert_key=e038dee1-a1d5-426d-ba4c-6bcd8c7100cb&p_cert_id=lbk0622&p_returntype=json"); /*URL*/
        String result = ApiCall(apiURL);

        JSONArray parse_item = parseData(result);

        List<PriceInfoResponseDto> priceInfoResponseDtoList = new ArrayList<>();

        for (int i = (parse_item.size()/3)*2; i-(parse_item.size()/3)*2 < (parse_item.size())/3; i++) {
            PriceInfoResponseDto priceInfoResponseDto = new PriceInfoResponseDto();

            JSONObject priceObj = (JSONObject) parse_item.get(i);

            priceInfoResponseDto.setDate(priceObj.get("regday").toString());
            priceInfoResponseDto.setPrice(priceObj.get("price").toString());

            priceInfoResponseDtoList.add(priceInfoResponseDto);
        }
        return priceInfoResponseDtoList;
    }

    //당일, 1일전, 1주전, 2주전, 1달전, 1년전 가격정보
    public PriceInfoResponseDto2 getPr() throws IOException, ParseException {

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=dailyPriceByCategoryList&p_product_cls_code=02&p_country_code=1101&p_regday=2022-06-07&p_convert_kg_yn=Y&p_item_category_code=200&p_cert_key=e038dee1-a1d5-426d-ba4c-6bcd8c7100cb&p_cert_id=lbk0622&p_returntype=json"); /*URL*/
        String result = ApiCall(apiURL);

        JSONArray parse_item = parseData(result);

        PriceInfoResponseDto2 priceInfoResponseDto2 = new PriceInfoResponseDto2();
        for (int i = 0; i < parse_item.size(); i++) {
            JSONObject a = (JSONObject) parse_item.get(i);
            if(a.get("item_code").toString().equals("212")) {
                if(a.get("rank_code").toString().equals("04")){
                    priceInfoResponseDto2.setDpr1(a.get("dpr1").toString());
                    priceInfoResponseDto2.setDpr2(a.get("dpr2").toString());
                    priceInfoResponseDto2.setDpr3(a.get("dpr3").toString());
                    priceInfoResponseDto2.setDpr4(a.get("dpr4").toString());
                    priceInfoResponseDto2.setDpr5(a.get("dpr5").toString());
                    priceInfoResponseDto2.setDpr6(a.get("dpr6").toString());
                }
            }
        }
        return priceInfoResponseDto2;
    }

    public static void main(String[] args) throws IOException, ParseException {
//    public void monthlyPrice() throws IOException, ParseException {
        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=monthlySalesList&p_yyyy=2022&p_period=2&p_itemcategorycode=100&p_itemcode=111&p_kindcode=01&p_graderank=1&p_countycode=1101&p_convert_kg_yn=Y&p_cert_key=e038dee1-a1d5-426d-ba4c-6bcd8c7100cb&p_cert_id=lbk0622&p_returntype=json"); /*URL*/
        String result = ApiCall(apiURL);

        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(result);
//        JSONObject parse_data = (JSONObject) obj.get("data");
//        JSONArray parse_item = (JSONArray) parse_data.get("item");
        System.out.println(obj);


    }


    public static String ApiCall(StringBuilder apiURL) throws IOException {
        StringBuilder urlBuilder = apiURL;

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
        return result;
    }

    public static JSONArray parseData(String result) throws ParseException {

        // Json parser를 만들어 만들어진 문자열 데이터를 객체화
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(result);
        JSONObject parse_data = (JSONObject) obj.get("data");
        JSONArray parse_item = (JSONArray) parse_data.get("item");

        return parse_item;
    }

}
