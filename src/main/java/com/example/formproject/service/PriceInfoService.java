package com.example.formproject.service;

import com.example.formproject.dto.request.PriceInfoRequestDto;
import com.example.formproject.dto.request.PriceInfoRequestDto2;
import com.example.formproject.dto.response.DailyPriceInfoDto;
import com.example.formproject.dto.response.MonthlyPriceInfoDto;
import com.example.formproject.dto.response.PriceInfoResponseDto;
import com.example.formproject.dto.response.YearlyPriceInfoDto;
import com.example.formproject.entity.Crop;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.security.MemberDetail;
import io.swagger.v3.core.util.Json;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PriceInfoService {
    private final OpenApiService openApiService;
    private final CropRepository cropRepository;
    @Value("e038dee1-a1d5-426d-ba4c-6bcd8c7100cb")
    private static String apiKey;
    @Value("lbk0622")
    private static String certId;

    //메인 페이지 시세정보 호출
    public PriceInfoResponseDto mainPriceInfo(PriceInfoRequestDto2 priceInfoRequestDto2, MemberDetail memberdetail) throws IOException, ParseException {
        Crop crop = cropRepository.findById(priceInfoRequestDto2.getCropId()).orElseThrow(null);

        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceInfoRequestDto2, memberdetail);

        DailyPriceInfoDto dailyPrice = dailyPrice(priceInfoRequestDto);
        MonthlyPriceInfoDto monthlyPriceInfoDto = monthlyPrice(priceInfoRequestDto);

        PriceInfoResponseDto priceInfoResponseDto = new PriceInfoResponseDto(dailyPrice, monthlyPriceInfoDto, priceInfoRequestDto2);
/*        priceInfoResponseDto.setPriceList(monthlyPriceInfoDto.getPriceList());
        priceInfoResponseDto.setDateList(monthlyPriceInfoDto.getDateList());
        priceInfoResponseDto.setUnit(monthlyPriceInfoDto.getUnit());
        priceInfoResponseDto.setCountry(dailyPrice.getCountyname());
        priceInfoResponseDto.setWholesale(priceInfoRequestDto2.getProductClsCode());
        if(dailyPrice.getPrice() != null) {
            priceInfoResponseDto.setLatestDate(dailyPrice.getYear());
            priceInfoResponseDto.setLatestDatePrice(dailyPrice.getPrice());
        } else {
            if (!priceInfoResponseDto.getPriceList().isEmpty()) {
                priceInfoResponseDto.setLatestDate(priceInfoResponseDto.getDateList().get(0));
                priceInfoResponseDto.setLatestDatePrice(priceInfoResponseDto.getPriceList().get(0));
            }
        }*/

        return priceInfoResponseDto;
    }

    //일별 시세
    public DailyPriceInfoDto dailyPrice(PriceInfoRequestDto priceInfoRequestDto) throws IOException, ParseException {
        //<editor-fold desc="날짜 및 변수선언">
        String startDay = dailyDate()[0];
        String endDay = dailyDate()[1];
        DailyPriceInfoDto dailyPriceInfoDto = new DailyPriceInfoDto();
        //</editor-fold>

        //<editor-fold desc="요청변수">
        String p_startday = startDay;
        String p_endday = endDay;
        String p_itemcategorycode = priceInfoRequestDto.getCategory() + "";
        String p_itemcode = priceInfoRequestDto.getType() + "";
        String p_kindcode = priceInfoRequestDto.getKind();
//        String p_productrankcode = "04";
        String p_productrankcode = (priceInfoRequestDto.getGradeRank().equals("상품")) ? "04" : "05";
        String p_countrycode = priceInfoRequestDto.getCountryCode()+"";
//        String p_countrycode = "1101";
        String p_productclscode = (priceInfoRequestDto.getProductClsCode().equals("소매")) ? "01" : "02";
        //</editor-fold>

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=periodProductList&p_productclscode=" + p_productclscode + "&p_startday=" + p_startday + "&p_endday=" + p_endday + "&p_itemcategorycode=" + p_itemcategorycode + "&p_itemcode=" + p_itemcode + "&p_kindcode=" + p_kindcode + "&p_productrankcode=" + p_productrankcode + "&p_countrycode=" + p_countrycode + "&p_convert_kg_yn=Y&p_cert_key=e038dee1-a1d5-426d-ba4c-6bcd8c7100cb&p_cert_id=lbk0622&p_returntype=json"); /*URL*/
        JSONObject obj = openApiService.ApiCall(apiURL);

        if (obj.get("data").getClass().getSimpleName().equals("JSONObject")) {
            JSONObject parse_data = (JSONObject) obj.get("data");
            JSONArray parse_item = (JSONArray) parse_data.get("item");

            JSONObject parse_latestDate = (JSONObject) parse_item.get(parse_item.size() - 1);

            dailyPriceInfoDto.setCountyname(parse_latestDate.get("countyname").toString());
            dailyPriceInfoDto.setYear(parse_latestDate.get("yyyy").toString() + "-" + parse_latestDate.get("regday").toString().replace("/", "-"));
            dailyPriceInfoDto.setPrice(parse_latestDate.get("price").toString());

            return dailyPriceInfoDto;
        } else {
            return dailyPriceInfoDto;
        }
    }

    //월별 시세
    public MonthlyPriceInfoDto monthlyPrice(PriceInfoRequestDto priceInfoRequestDto) throws IOException, ParseException {
        MonthlyPriceInfoDto monthlyPriceInfoDto = new MonthlyPriceInfoDto();
        List<String> priceList = new ArrayList<>();

        //<editor-fold desc="요청 변수">
        String productclscode = (priceInfoRequestDto.getProductClsCode().equals("소매")) ? "01" : "02";
        String categoryCode = priceInfoRequestDto.getCategory() + "";
        String itemCode = priceInfoRequestDto.getType() + "";
        String kindCode = priceInfoRequestDto.getKind();
//        String gradeRank = "1";
        String gradeRank = (priceInfoRequestDto.getGradeRank().equals("상품")) ? "1" : "2";
        String countyCode = priceInfoRequestDto.getCountryCode()+"";
//        String countyCode = "1101";
        int month = priceInfoRequestDto.getMonth();
        String nowYear = priceInfoRequestDto.getYear() + "";
        //</editor-fold>

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=monthlySalesList&p_yyyy=" + nowYear + "&p_period=3&p_itemcategorycode=" + categoryCode + "&p_itemcode=" + itemCode + "&p_kindcode=" + kindCode + "&p_graderank=" + gradeRank + "&p_countycode=" + countyCode + "&p_convert_kg_yn=Y&p_cert_key=" + apiKey + "&p_cert_id=" + certId + "&p_returntype=json"); //URL

        JSONObject obj = openApiService.ApiCall(apiURL);

        JSONArray parse_price = (JSONArray) obj.get("price");

        for (int i = 0; i < parse_price.size(); i++) {
            JSONObject parse_date = (JSONObject) parse_price.get(i);
            String clsCode = parse_date.get("productclscode").toString();
            if (clsCode.equals(productclscode)) {
                String unit = parse_date.get("caption").toString().split("> ")[5];

                if (unit.replaceAll("[^\\d]", "").equals("1")) {
                    unit = unit.substring(1);
                }
                monthlyPriceInfoDto.setUnit(unit);

                JSONArray monthPriceOfThreeYear = (JSONArray) parse_date.get("item");
                if (monthPriceOfThreeYear == null) {
                    return monthlyPriceInfoDto;
                }
                priceList = monthlyPriceList(monthPriceOfThreeYear, month);
            }
        }
        Collections.reverse(priceList);
        String select = "month";
        monthlyPriceInfoDto.setDateList(makeDateList(select));
        monthlyPriceInfoDto.setPriceList(priceList);
        return monthlyPriceInfoDto;
    }

    //연도별 시세
    public static void main(String[] args) throws IOException, ParseException {
        String select = "year";

        List<String[]> sumDataList = new ArrayList<>();
        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=yearlySalesList&p_yyyy=2022&p_itemcategorycode=100&p_itemcode=414&p_kindcode=10&p_graderank=1&p_countycode=1101&p_convert_kg_yn=N&p_cert_key=111&p_cert_id=222&p_returntype=json"); //URL
        String result = ApiCall(apiURL);


        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(result);
        JSONArray parse_price = (JSONArray) obj.get("price");

        List<String> yearlyPriceList = new ArrayList<>();
        List<String> yearlyList = makeDateList(select);
        for (int i = 0; i < parse_price.size(); i++) {
            JSONObject parse_date = (JSONObject) parse_price.get(i);
            String clsCode = parse_date.get("productclscode").toString();
            if (clsCode.equals("01")) {
                JSONArray yearlyPrice = (JSONArray) parse_date.get("item");
                for (int j = 0; j < yearlyPrice.size(); j++) {
                    JSONObject year = (JSONObject) yearlyPrice.get(j);
                    if (!year.get("div").equals("평년")){
                        String[] dataList = new String[2];
                        dataList[0] = year.get("div").toString();
                        dataList[1] = year.get("avg_data").toString();
                        sumDataList.add(dataList);
                    }
                }
            }
        }

        for (int i = 0; i < yearlyList.size(); i++) {
            boolean ok = true;
            for (int j = 0; j < sumDataList.size(); j++) {

                if (yearlyList.get(i).equals(sumDataList.get(j)[0])) {
                    yearlyPriceList.add(sumDataList.get(j)[1]);
                    ok = false;
                }
            }
            if (ok) {
                yearlyPriceList.add("0");
            }
        }
        YearlyPriceInfoDto yearlyPriceInfoDto = new YearlyPriceInfoDto(yearlyList, yearlyPriceList);


    }

    //API 호출
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

    //월별 시세에서 파싱한 데이터 리스트화
    public static List<String> makeList(JSONArray a) {
        List<String> totalList = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            JSONObject o = (JSONObject) a.get(i);
            for (int j = 1; j < o.size() - 1; j++) {
                String mData = o.get("m" + j).toString();
                totalList.add(mData);
            }
        }
        return totalList;
    }

    //일별 시세에 필요한 날짜정보(1주일)
    public static String[] dailyDate() {
        String[] time = new String[2];
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -7);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        time[0] = sdf.format(cal.getTime());
        time[1] = sdf.format(today);

        return time;
    }

    //월별 시세에서 비어있는 데이터 채워넣고 2개월 단위로 추출
    public static List<String> monthlyPriceList(JSONArray monthPriceOfThreeYear, int month) {
        List<String> sumList = new ArrayList<>();
        List<String> priceList = new ArrayList<>();
        List<String> finalList = new ArrayList<>();

        JSONArray threeYears = new JSONArray();
        for (int j = 1; j < monthPriceOfThreeYear.size(); j++) {
            threeYears.add(monthPriceOfThreeYear.get(j));
        }

        sumList = makeList(threeYears);

        for (int j = sumList.size() - 1; j > 0; j--) {
            if (j != 1 && sumList.get(j).equals("-")) {
                for (int k = j - 1; k > 0; k--) {
                    if (!sumList.get(k).equals("-")) {
                        sumList.set(j, sumList.get(k));
                        break;
                    }
                }
            }
        }
        for (int l = 12; l < 36; l++) {
            finalList.add(sumList.get(l));
        }

        for (int j = month + 11; j + 12 >= month + 11; j -= 2) {
            priceList.add(finalList.get(j));
        }
        return priceList;
    }

    // 월별/연도별 날짜 리스트 생성
    public static List<String> makeDateList(String select) {
        List<String> dateList = new ArrayList<>();

        DateFormat sdf = new SimpleDateFormat("yyyy-MM");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        if (select.equals("month")) {
            cal.add(Calendar.MONTH, 0);
            for (int i = 0; i < 7; i++) {
                String date = sdf.format(cal.getTime());

                cal.add(Calendar.MONTH, -2);
                dateList.add(date);
            }
        }

        if (select.equals("year")) {
            cal.add(Calendar.YEAR, 0);
            for (int i = 0; i < 6; i++) {
                String date = sdf.format(cal.getTime()).split("-")[0];

                cal.add(Calendar.YEAR, -1);
                dateList.add(date);
            }
        }

        Collections.reverse(dateList);
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
