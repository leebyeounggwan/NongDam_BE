package com.example.formproject.service;

import com.example.formproject.annotation.UseCache;
import com.example.formproject.dto.request.PriceApiRequestVariableDto;
import com.example.formproject.dto.request.PriceInfoRequestDto;
import com.example.formproject.dto.response.DailyPriceResponseDto;
import com.example.formproject.dto.response.PriceInfoDto;
import com.example.formproject.dto.response.PriceMetaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class PriceInfoService {
    private final OpenApiService openApiService;
    @Value("e038dee1-a1d5-426d-ba4c-6bcd8c7100cb")
    private static String apiKey;
    @Value("lbk0622")
    private static String certId;

    //일별 시세
//    @UseCache(cacheKey = "cacheKey", ttl = -1L,unit = TimeUnit.HOURS,timeData = false)
    public DailyPriceResponseDto dailyPrice(PriceInfoRequestDto priceInfoRequestDto, String cacheKey) throws ParseException {
        String data = "day";
        List<String> dailyDate = makeDateList(data);
        PriceApiRequestVariableDto var = new PriceApiRequestVariableDto(priceInfoRequestDto, dailyDate);
        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=periodProductList&p_productclscode=" + var.getClsCode() + "&p_startday=" + var.getStartDay() + "&p_endday=" + var.getEndDay() + "&p_itemcategorycode=" + var.getCategoryCode() + "&p_itemcode=" + var.getItemCode() + "&p_kindcode=" + var.getKindCode() + "&p_productrankcode=" + var.getGradeRank() + "&p_countrycode=" + var.getCountryCode() + "&p_convert_kg_yn=Y&p_cert_key="+apiKey+"&p_cert_id="+certId+"&p_returntype=json");

        try {
            JSONObject obj = openApiService.ApiCall(apiURL);
            if (obj.get("data").getClass().getSimpleName().equals("JSONObject")) {
                PriceMetaDto meta = new PriceMetaDto(priceInfoRequestDto, obj, data);
                return new DailyPriceResponseDto(meta, obj);
            } else {
                return new DailyPriceResponseDto(new PriceMetaDto(priceInfoRequestDto));
            }
        }
        catch (IOException | NullPointerException | ClassCastException | IndexOutOfBoundsException e) {
            return new DailyPriceResponseDto(new PriceMetaDto(priceInfoRequestDto));
        }
    }

    //월별 시세
//    @UseCache(cacheKey = "cacheKey", ttl = -1L,unit = TimeUnit.HOURS,timeData = false)
    public List<PriceInfoDto> monthAndYearPrice(PriceInfoRequestDto priceInfoRequestDto, String cacheKey) throws ParseException {
        String data = priceInfoRequestDto.getData();
        List<String> setDateList = makeDateList(data);
        Collections.reverse(setDateList);
        List<PriceInfoDto> priceList = new ArrayList<>();
        PriceApiRequestVariableDto var = new PriceApiRequestVariableDto(priceInfoRequestDto);

        StringBuilder apiURL = new StringBuilder();
        if (data.equals("month")){
            apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=monthlySalesList&p_yyyy=" + var.getNowYear() + "&p_period=3&p_itemcategorycode=" + var.getCategoryCode() + "&p_itemcode=" + var.getItemCode() + "&p_kindcode=" + var.getKindCode() + "&p_graderank=" + var.getGradeRank() + "&p_countycode=" + var.getCountryCode() + "&p_convert_kg_yn=Y&p_cert_key=" + apiKey + "&p_cert_id=" + certId + "&p_returntype=json"); //URL
        }
        if (data.equals("year")){
            apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=yearlySalesList&p_yyyy="+var.getNowYear()+"&p_itemcategorycode="+var.getCategoryCode()+"&p_itemcode="+var.getItemCode()+"&p_kindcode="+var.getKindCode()+"&p_graderank="+var.getGradeRank()+"&p_countycode="+var.getCountryCode()+"&p_convert_kg_yn=Y&p_cert_key="+apiKey+"&p_cert_id="+certId+"&p_returntype=json"); //URL
        }

        try {
            JSONObject obj = openApiService.ApiCall(apiURL);
            JSONArray parse_price = new JSONArray();
            parse_price = checkType(obj, parse_price);

            for (int i = 0; i < parse_price.size(); i++) {
                JSONObject parse_date = (JSONObject) parse_price.get(i);
                PriceMetaDto meta = new PriceMetaDto(priceInfoRequestDto, parse_date, data);
                PriceInfoDto priceInfoDto = new PriceInfoDto(priceInfoRequestDto, meta, setDateList, parse_date);
                priceList.add(priceInfoDto);
                if (parse_price.size() == 1) {
                    plusOne(meta, priceList, priceInfoDto);
                }
            }
        } catch (IOException | NullPointerException | ClassCastException | IndexOutOfBoundsException e) {
            for (int i = 0; i < 2; i++) {
                PriceMetaDto meta = new PriceMetaDto(priceInfoRequestDto);
                PriceInfoDto monthPriceInfoDto = new PriceInfoDto(meta, i);
                priceList.add(monthPriceInfoDto);
            }
        }
        return priceList;
    }

    // 도,소매 둘중에 하나만 있는 경우
    private void plusOne(PriceMetaDto meta, List<PriceInfoDto> priceList, PriceInfoDto priceInfoDto) {
        if (priceInfoDto.getWholeSale().equals("도매")) {
            priceList.add(new PriceInfoDto(meta, 1));
        } else if (priceInfoDto.getWholeSale().equals("소매")) {
            priceList.add(new PriceInfoDto(meta, 0));
            Collections.reverse(priceList);
        }
    }

    // 타입 체크
    private JSONArray checkType(JSONObject obj, JSONArray parse_price) {
        if (obj.get("price").getClass().getSimpleName().equals("JSONObject")) {
            parse_price.add(obj.get("price"));
        } else {
            parse_price = (JSONArray) obj.get("price");
        }
        return parse_price;
    }

    // 일별/월별/연도별 날짜 리스트 생성
    public List<String> makeDateList(String select) {
        List<String> dateList = new ArrayList<>();

        Date today = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        switch (select) {
            case "day":
                String endDay = sdf.format(today);
                cal.add(Calendar.MONTH, -1);
                String startDay = sdf.format(cal.getTime());
                dateList.add(startDay);
                dateList.add(endDay);
                break;
            case "month":
                cal.add(Calendar.MONTH, 0);
                for (int i = 0; i < 7; i++) {
                    String date = sdf.format(cal.getTime()).split("-")[0]+"."+sdf.format(cal.getTime()).split("-")[1];
                    cal.add(Calendar.MONTH, -2);
                    dateList.add(date);
                }
                break;
            case "year":
                cal.add(Calendar.YEAR, 0);
                for (int i = 0; i < 6; i++) {
                    String date = sdf.format(cal.getTime()).split("-")[0];
                    cal.add(Calendar.YEAR, -1);
                    dateList.add(date);
                }
                break;
        }
        return dateList;
    }
}
