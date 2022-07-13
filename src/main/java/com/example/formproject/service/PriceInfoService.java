package com.example.formproject.service;

import com.example.formproject.dto.request.PriceInfoRequestDto;
import com.example.formproject.dto.response.DailyPriceInfoDto;
import com.example.formproject.dto.response.PriceInfoDto;
import com.example.formproject.enums.CountryCode;
import com.example.formproject.enums.CropTypeCode;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.formproject.enums.CountryCode.findByCountryCode;
import static com.example.formproject.enums.CropTypeCode.findByCode;

@Service
@RequiredArgsConstructor
public class PriceInfoService {

    private final OpenApiService openApiService;
    @Value("e038dee1-a1d5-426d-ba4c-6bcd8c7100cb")
    private static String apiKey;
    @Value("lbk0622")
    private static String certId;

    //일별 시세
    public DailyPriceInfoDto dailyPrice(PriceInfoRequestDto priceInfoRequestDto) throws IOException, ParseException {
        //<editor-fold desc="날짜 및 DTO 선언">
        List<String> dailyDate = makeDateList("day");
        DailyPriceInfoDto dailyPriceInfoDto = new DailyPriceInfoDto();
        //</editor-fold>

        //<editor-fold desc="요청변수">
        String p_startday = dailyDate.get(0);
        String p_endday = dailyDate.get(1);
        String p_itemcategorycode = priceInfoRequestDto.getCategory() + "";
        String p_itemcode = priceInfoRequestDto.getType() + "";
        String p_kindcode = priceInfoRequestDto.getKind();
//        String p_productrankcode = (priceInfoRequestDto.getGradeRank().equals("상품")) ? "04" : "05";
        String p_productrankcode = "04";
        String p_countrycode = (priceInfoRequestDto.getCountryCode().equals("0")) ? "1101" : priceInfoRequestDto.getCountryCode();
        String p_productclscode = (priceInfoRequestDto.getProductClsCode().equals("소매")) ? "01" : "02";
        //</editor-fold>

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=periodProductList&p_productclscode=" + p_productclscode + "&p_startday=" + p_startday + "&p_endday=" + p_endday + "&p_itemcategorycode=" + p_itemcategorycode + "&p_itemcode=" + p_itemcode + "&p_kindcode=" + p_kindcode + "&p_productrankcode=" + p_productrankcode + "&p_countrycode=" + p_countrycode + "&p_convert_kg_yn=Y&p_cert_key=e038dee1-a1d5-426d-ba4c-6bcd8c7100cb&p_cert_id=lbk0622&p_returntype=json"); /*URL*/

        try {
            JSONObject obj = openApiService.ApiCall(apiURL);

            if (obj.get("data").getClass().getSimpleName().equals("JSONObject")) {
                JSONObject parse_data = (JSONObject) obj.get("data");
                JSONArray parse_item = (JSONArray) parse_data.get("item");

                JSONObject parse_latestDate = (JSONObject) parse_item.get(parse_item.size() - 1);
                String unit = parse_latestDate.get("kindname").toString().split("\\(")[1].replaceAll("\\)", "");
                if (!unit.contains("kg")) {
                    unit = "kg";
                }
                if (unit.replaceAll("[^\\d]", "").equals("1")) {
                    unit = unit.substring(1);
                }
                dailyPriceInfoDto.setCrop(parse_latestDate.get("itemname").toString());
                dailyPriceInfoDto.setType(priceInfoRequestDto.getName());
                dailyPriceInfoDto.setUnit(unit);
                dailyPriceInfoDto.setCountry(parse_latestDate.get("countyname").toString());
                dailyPriceInfoDto.setWholeSale(priceInfoRequestDto.getProductClsCode());
                dailyPriceInfoDto.setLatestDate(parse_latestDate.get("yyyy").toString() + "-" + parse_latestDate.get("regday").toString().replace("/", "-"));
                dailyPriceInfoDto.setLatestDatePrice(parse_latestDate.get("price").toString());

                return dailyPriceInfoDto;
            } else {
                dailyPriceInfoDto.setCrop(findCropName(p_itemcode));
                dailyPriceInfoDto.setType(priceInfoRequestDto.getName());
                dailyPriceInfoDto.setUnit("kg");
                dailyPriceInfoDto.setCountry(findCountryName(p_countrycode));
                dailyPriceInfoDto.setWholeSale(priceInfoRequestDto.getProductClsCode());
                dailyPriceInfoDto.setLatestDate("");
                dailyPriceInfoDto.setLatestDatePrice("");
                return dailyPriceInfoDto;
            }
        }
        catch (IOException e) {
            dailyPriceInfoDto.setCrop(findCropName(p_itemcode));
            dailyPriceInfoDto.setType(priceInfoRequestDto.getName());
            dailyPriceInfoDto.setUnit("kg");
            dailyPriceInfoDto.setCountry(findCountryName(p_countrycode));
            dailyPriceInfoDto.setWholeSale(priceInfoRequestDto.getProductClsCode());
            dailyPriceInfoDto.setLatestDate("");
            dailyPriceInfoDto.setLatestDatePrice("");
            return dailyPriceInfoDto;
        }
    }

    //월별 시세
    public List<PriceInfoDto> monthlyPrice(PriceInfoRequestDto priceInfoRequestDto) throws IOException, ParseException {

        List<PriceInfoDto> monthlyPriceList = new ArrayList<>();

        int month = priceInfoRequestDto.getMonth();

        //<editor-fold desc="요청 변수">
        //String productclscode = (priceInfoRequestDto.getProductClsCode().equals("소매")) ? "01" : "02";
        String categoryCode = priceInfoRequestDto.getCategory() + "";
        String itemCode = priceInfoRequestDto.getType() + "";
        String kindCode = priceInfoRequestDto.getKind();
//        String gradeRank = (priceInfoRequestDto.getGradeRank().equals("상품")) ? "1" : "2";
        String gradeRank = "1";
        String countyCode = (priceInfoRequestDto.getCountryCode().equals("0")) ? "1101" : priceInfoRequestDto.getCountryCode();
        String nowYear = priceInfoRequestDto.getYear() + "";
        //</editor-fold>

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=monthlySalesList&p_yyyy=" + nowYear + "&p_period=3&p_itemcategorycode=" + categoryCode + "&p_itemcode=" + itemCode + "&p_kindcode=" + kindCode + "&p_graderank=" + gradeRank + "&p_countycode=" + countyCode + "&p_convert_kg_yn=Y&p_cert_key=" + apiKey + "&p_cert_id=" + certId + "&p_returntype=json"); //URL
        JSONObject obj = openApiService.ApiCall(apiURL);
        JSONArray parse_price = (JSONArray) obj.get("price");
// 월별 -> 도소매 다 조회되는데 없을 경우 아예 빈리스트가 넘어감
        for (int i = 0; i < parse_price.size(); i++) {
            PriceInfoDto monthPriceInfoDto = new PriceInfoDto();
            JSONObject parse_date = (JSONObject) parse_price.get(i);
            String clsCode = (parse_date.get("productclscode").toString().equals("01")) ? "소매" : "도매";
            String[] stringa = parse_date.get("caption").toString().split(" > ");

            String unit = stringa[5];

            if (unit.replaceAll("[^\\d]", "").equals("1")) {
                unit = unit.substring(1);
            }
            monthPriceInfoDto.setUnit(unit);
            monthPriceInfoDto.setType(priceInfoRequestDto.getName());
            monthPriceInfoDto.setCountry(findCountryName(countyCode));
            monthPriceInfoDto.setCrop(stringa[2]);
            monthPriceInfoDto.setWholeSale(clsCode);
            JSONArray monthPriceOfThreeYear = (JSONArray) parse_date.get("item");
            if (monthPriceOfThreeYear == null) {
                List<String> list = Collections.emptyList();
                monthPriceInfoDto.setDateList(list);
                monthPriceInfoDto.setPriceList(list);
                monthlyPriceList.add(monthPriceInfoDto);
            } else {
                List<String> priceList = monthlyPriceList(monthPriceOfThreeYear, month);
                Collections.reverse(priceList);
                monthPriceInfoDto.setDateList(makeDateList("month"));
                monthPriceInfoDto.setPriceList(priceList);

                monthlyPriceList.add(monthPriceInfoDto);
            }
        }

        return monthlyPriceList;
    }

    //연도별 시세
    public List<PriceInfoDto> yearlyPrice(PriceInfoRequestDto priceInfoRequestDto) throws IOException, ParseException {

        List<PriceInfoDto> yearlyPriceList = new ArrayList<>();

        //<editor-fold desc="요청 변수">
        //String productclscode = (priceInfoRequestDto.getProductClsCode().equals("소매")) ? "01" : "02";
        String categoryCode = priceInfoRequestDto.getCategory() + "";
        String itemCode = priceInfoRequestDto.getType() + "";
        String kindCode = priceInfoRequestDto.getKind();
//        String gradeRank = (priceInfoRequestDto.getGradeRank().equals("상품")) ? "1" : "2";
        String gradeRank = "1";
        String countyCode = (priceInfoRequestDto.getCountryCode().equals("0")) ? "1101" : priceInfoRequestDto.getCountryCode();
        String nowYear = priceInfoRequestDto.getYear() + "";
        //</editor-fold>


        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=yearlySalesList&p_yyyy="+nowYear+"&p_itemcategorycode="+categoryCode+"&p_itemcode="+itemCode+"&p_kindcode="+kindCode+"&p_graderank="+gradeRank+"&p_countycode="+countyCode+"&p_convert_kg_yn=Y&p_cert_key="+apiKey+"&p_cert_id="+certId+"&p_returntype=json"); //URL

        JSONObject obj = openApiService.ApiCall(apiURL);
        JSONArray parse_price = (JSONArray) obj.get("price");

// 연도별 -> 도소매 다 조회되는데 없을경우 classcastexection이 뜬다.
        List<String> dateList = makeDateList("year");
        for (int i = 0; i < parse_price.size(); i++) {
            PriceInfoDto yearPriceInfoDto = new PriceInfoDto();
            List<String[]> sumDataList = new ArrayList<>();
            List<String> yearpriceList = new ArrayList<>();
            JSONObject parse_date = (JSONObject) parse_price.get(i);
            String clsCode = (parse_date.get("productclscode").toString().equals("01")) ? "소매" : "도매";
            String[] stringa = parse_date.get("caption").toString().split(" > ");

            String unit = stringa[5];

            if (unit.replaceAll("[^\\d]", "").equals("1")) {
                unit = unit.substring(1);
            }
            yearPriceInfoDto.setCrop(stringa[2]);
            yearPriceInfoDto.setType(priceInfoRequestDto.getName());
            yearPriceInfoDto.setUnit(unit);
            yearPriceInfoDto.setCountry(findCountryName(countyCode));
            yearPriceInfoDto.setWholeSale(clsCode);

            if (parse_date.get("item").getClass().getSimpleName().equals("JSONArray")) {
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
                for (int k = 0; k < dateList.size(); k++) {
                    boolean ok = true;
                    for (int j = 0; j < sumDataList.size(); j++) {

                        if (dateList.get(k).equals(sumDataList.get(j)[0])) {
                            yearpriceList.add(sumDataList.get(j)[1]);
                            ok = false;
                        }
                    }
                    if (ok) {
                        yearpriceList.add("0");
                    }
                }
                yearPriceInfoDto.setDateList(makeDateList("year"));
                yearPriceInfoDto.setPriceList(yearpriceList);

                yearlyPriceList.add(yearPriceInfoDto);
            } else {
                List<String> list = Collections.emptyList();
                yearPriceInfoDto.setDateList(list);
                yearPriceInfoDto.setPriceList(list);
                yearlyPriceList.add(yearPriceInfoDto);
            }
        }
        return yearlyPriceList;
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

    //월별 시세에서 비어있는 데이터 채워넣고 2개월 단위로 추출
    public static List<String> monthlyPriceList(JSONArray monthPriceOfThreeYear, int month) {
        List<String> sumList = new ArrayList<>();
        List<String> mPriceList = new ArrayList<>();
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
            mPriceList.add(finalList.get(j));
        }
        return mPriceList;
    }

    // 일별/월별/연도별 날짜 리스트 생성
    public static List<String> makeDateList(String select) {
        List<String> dateList = new ArrayList<>();

        Date today = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        if (select.equals("day")) {
            String endDay = sdf.format(today);
            cal.add(Calendar.MONTH, -1);
            String startDay = sdf.format(cal.getTime());
            dateList.add(startDay);
            dateList.add(endDay);
        }

        if (select.equals("month")) {
            cal.add(Calendar.MONTH, 0);
            for (int i = 0; i < 7; i++) {
                String date = sdf.format(cal.getTime()).split("-")[0]+"-"+sdf.format(cal.getTime()).split("-")[1];

                cal.add(Calendar.MONTH, -2);
                dateList.add(date);
            }
            Collections.reverse(dateList);
        }

        if (select.equals("year")) {
            cal.add(Calendar.YEAR, 0);
            for (int i = 0; i < 6; i++) {
                String date = sdf.format(cal.getTime()).split("-")[0];

                cal.add(Calendar.YEAR, -1);
                dateList.add(date);
            }
            Collections.reverse(dateList);
        }
        return dateList;
    }
    // 1101(countryCode) -> 서울
    public static String findCountryName(String countryCode) {
        int num = Integer.parseInt(countryCode);
        CountryCode byCode = findByCountryCode(num);

        return byCode.name();
    }

    public static String findCropName(String cropCode) {
        int num = Integer.parseInt(cropCode);
        CropTypeCode byCode = findByCode(num);
        return byCode.toString();
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
