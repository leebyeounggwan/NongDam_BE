package com.example.formproject.service;

import com.example.formproject.dto.request.PriceInfoRequestDto;
import com.example.formproject.dto.response.PriceInfoResponseDto;
import com.example.formproject.entity.Crop;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.security.MemberDetail;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PriceInfoService {

    private final CropRepository cropRepository;

    @Value("e038dee1-a1d5-426d-ba4c-6bcd8c7100cb")
    private static String apiKey;
    @Value("lbk0622")
    private static String certId;

    public PriceInfoResponseDto priceInfo(String productClsCode, String gradeRank, int cropId, MemberDetail memberdetail) throws IOException, ParseException {
        Crop crop = cropRepository.findById(cropId).orElseThrow(null);
        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, productClsCode, gradeRank, memberdetail);
        PriceInfoResponseDto priceInfoResponseDto = new PriceInfoResponseDto();
        List<String> priceList = monthlyPrice(priceInfoRequestDto, priceInfoResponseDto);
        int todayPrice = dailyPrice(priceInfoRequestDto);
        priceInfoResponseDto.setProductClsCode(priceInfoRequestDto.getProductClsCode());
        priceInfoResponseDto.setItemCode(priceInfoRequestDto.getCategory()+"");
        priceInfoResponseDto.setKindCode(priceInfoRequestDto.getKind());
        priceInfoResponseDto.setRankCode(priceInfoRequestDto.getGradeRank());
        priceInfoResponseDto.setMarket(priceInfoRequestDto.getCountryCode()+"");
        priceInfoResponseDto.setTodayPrice(todayPrice);
        priceInfoResponseDto.setPriceList(priceList);
        priceInfoResponseDto.setDateList(makeDateList());

        return priceInfoResponseDto;
    }

    public int dailyPrice(PriceInfoRequestDto priceInfoRequestDto) throws IOException, ParseException {

        Date today = new Date();
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(today);

        String p_startday = formattedDate;
        String p_endday = formattedDate;
        String p_itemcategorycode = priceInfoRequestDto.getCategory()+"";
        String p_itemcode = priceInfoRequestDto.getType()+"";
        String p_kindcode = priceInfoRequestDto.getKind();
        String p_productrankcode = priceInfoRequestDto.getProductClsCode();
        String p_countrycode = priceInfoRequestDto.getCountryCode()+"";

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=periodProductList&p_productclscode=02&p_startday="+p_startday+"&p_endday="+p_endday+"&p_itemcategorycode="+p_itemcategorycode+"&p_itemcode="+p_itemcode+"&p_kindcode="+p_kindcode+"&p_productrankcode="+p_productrankcode+"&p_countrycode="+p_countrycode+"&p_convert_kg_yn=Y&p_cert_key=e038dee1-a1d5-426d-ba4c-6bcd8c7100cb&p_cert_id=lbk0622&p_returntype=json"); /*URL*/
        String result = ApiCall(apiURL);

        JSONArray parse_item = parseData(result);

        int todayPrice=0;
        for (int i = (parse_item.size()/3)*2; i-(parse_item.size()/3)*2 < (parse_item.size())/3; i++) {
            PriceInfoResponseDto priceInfoResponseDto = new PriceInfoResponseDto();

            JSONObject priceObj = (JSONObject) parse_item.get(i);

//            priceInfoResponseDto.setDate(priceObj.get("regday").toString());
//            priceInfoResponseDto.setPrice(priceObj.get("price").toString());
            todayPrice = (Integer) priceObj.get("price");
        }
        return todayPrice;
    }

    /*      1. memberdetail에서 유저정보 가져오고 유저정보에서 작물정보 가져온다.
            2. 작물정보를 등록하지 않은 상태이면 기본값으로 보여준다.
            3. 작물정보 중 카테고리, 품목, 이름 세개가 다 있는지 확인해서 있으면
            해당 작물에 대한 시세를 상품(일단 기본값으로) 보여준다.
            4. 도/소매 구분값 필요
            - 도/소매, 등급은 프론트에게 전달받고 나머지는 유저정보에서
*/
    //월별 시세정보
//    public static void main(String[] args) throws IOException, ParseException {
    public List<String> monthlyPrice(PriceInfoRequestDto priceInfoRequestDto, PriceInfoResponseDto priceInfoResponseDto) throws IOException, ParseException {

        String productclscode = priceInfoRequestDto.getProductClsCode();
        String categoryCode = priceInfoRequestDto.getCategory()+"";
        String itemCode = priceInfoRequestDto.getType()+"";
        String kindCode = priceInfoRequestDto.getKind();
        String gradeRank = priceInfoRequestDto.getGradeRank();
        String countyCode = priceInfoRequestDto.getCountryCode()+"";
        int year = priceInfoRequestDto.getYear();
        int month = priceInfoRequestDto.getMonth();
        String nowYear = year + "";

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=monthlySalesList&p_yyyy="+nowYear+"&p_period=3&p_itemcategorycode="+categoryCode+"&p_itemcode="+itemCode+"&p_kindcode="+kindCode+"&p_graderank="+gradeRank+"&p_countycode="+countyCode+"&p_convert_kg_yn=Y&p_cert_key="+apiKey+"&p_cert_id="+certId+"&p_returntype=json"); //URL
        String result = ApiCall(apiURL);

        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(result);
        JSONArray parse_price = (JSONArray) obj.get("price");

        List<String> priceList = new ArrayList<>();

        for (int i = 0; i < parse_price.size(); i++) {
            JSONObject parse_date = (JSONObject) parse_price.get(i);
            System.out.println(parse_date);
            String clsCode = parse_date.get("productclscode").toString();
            if (clsCode.equals(productclscode)) {
                String unit = parse_date.get("caption").toString().split("> ")[5];
                priceInfoResponseDto.setUnit(unit);
                JSONArray monthPriceOfThreeYear = (JSONArray) parse_date.get("item");
                JSONObject a = (JSONObject) monthPriceOfThreeYear.get(3);
                JSONObject b = (JSONObject) monthPriceOfThreeYear.get(2);
                System.out.println(a);
                System.out.println(b);
                String m = "m"+month;
                System.out.println(m);

                List<String> stringsA = makeList(a);
                List<String> stringsB = makeList(b);

                List<String> sumList = new ArrayList<>();
                sumList.addAll(stringsB);
                sumList.addAll(stringsA);


                for (int j = month+11; j+12 >= month+11; j-=2) {
                    priceList.add(sumList.get(j));
                }
                Collections.reverse(priceList);


            }
        }
        return priceList;
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

    public static List<String> makeList(JSONObject a) {

        List<String> totalList = new ArrayList<>();
        for (int i = 1; i < a.size()-1; i++) {
            String mData = a.get("m" + i).toString();
            totalList.add(mData);
        }

        return totalList;
    }


    public static List<String> makeDateList() {
        List<String> dateList = new ArrayList<>();

        Date now = new Date();
        System.out.println(now);

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MONTH,1);
        for (int i = 0; i < 7; i++) {
            String date = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
            cal.add(Calendar.MONTH,-2);
            System.out.println(date);
            dateList.add(date);
        }
        return dateList;
    }

}


/*
    //당일, 1일전, 1주전, 2주전, 1달전, 1년전 가격정보
    public PriceInfoResponseDto2 getPr() throws IOException, ParseException {

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=dailyPriceByCategoryList&p_product_cls_code=02&p_country_code=1101&p_regday=2022-06-07&p_convert_kg_yn=Y&p_item_category_code=200&p_cert_key=e038dee1-a1d5-426d-ba4c-6bcd8c7100cb&p_cert_id=lbk0622&p_returntype=json"); */
/*URL*//*

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
    }*/
