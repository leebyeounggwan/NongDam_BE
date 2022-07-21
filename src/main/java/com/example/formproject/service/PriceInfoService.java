package com.example.formproject.service;

import com.example.formproject.annotation.UseCache;
import com.example.formproject.dto.request.PriceApiRequestVariableDto;
import com.example.formproject.dto.request.PriceInfoRequestDto;
import com.example.formproject.dto.response.DailyPriceResponseDto;
import com.example.formproject.dto.response.PriceInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class PriceInfoService {
    private final PriceService priceService;
    private final OpenApiService openApiService;
    @Value("e038dee1-a1d5-426d-ba4c-6bcd8c7100cb")
    private static String apiKey;
    @Value("lbk0622")
    private static String certId;

    //일별 시세
    public DailyPriceResponseDto dailyPrice(PriceInfoRequestDto priceInfoRequestDto) throws ParseException {

        List<String> dailyDate = priceService.makeDateList("day");
        PriceApiRequestVariableDto var = new PriceApiRequestVariableDto(priceInfoRequestDto, dailyDate);
        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=periodProductList&p_productclscode=" + var.getClsCode() + "&p_startday=" + var.getStartDay() + "&p_endday=" + var.getEndDay() + "&p_itemcategorycode=" + var.getCategoryCode() + "&p_itemcode=" + var.getItemCode() + "&p_kindcode=" + var.getKindCode() + "&p_productrankcode=" + var.getGradeRank() + "&p_countrycode=" + var.getCountryCode() + "&p_convert_kg_yn=Y&p_cert_key="+apiKey+"&p_cert_id="+certId+"&p_returntype=json"); /*URL*/

        try {
            JSONObject obj = openApiService.ApiCall(apiURL);
            if (obj.get("data").getClass().getSimpleName().equals("JSONObject")) {
                JSONObject parse_data = (JSONObject) obj.get("data");
                JSONArray parse_item = (JSONArray) parse_data.get("item");
                //데이터 중 가장 마지막 인덱스(가장 최근 조사된 시세)
                JSONObject parse_latestDate = new JSONObject();
                JSONObject parse_unit = (JSONObject) parse_item.get(parse_item.size()-1);

                for (int i = 0; i < parse_item.size(); i++) {
                    parse_latestDate = (JSONObject) parse_item.get(i);
                    if (parse_latestDate.get("countyname").equals("평년")) {
                        parse_latestDate = (JSONObject) parse_item.get(i-1);
                        break;
                    }
                }
                String unit = parse_unit.get("kindname").toString().split("\\(")[1].replaceAll("\\)", "");
                if (unit.replaceAll("[^\\d]", "").equals("1")) {
                    unit = unit.substring(1);
                }
                return new DailyPriceResponseDto(priceInfoRequestDto,var.getItemCode(), var.getCountryCode(), parse_latestDate, unit);
            } else {
                return new DailyPriceResponseDto(priceInfoRequestDto, var.getItemCode(), var.getCountryCode());
            }
        }
        catch (IOException | NullPointerException | ClassCastException | IndexOutOfBoundsException e) {
            return new DailyPriceResponseDto(priceInfoRequestDto, var.getItemCode(), var.getCountryCode());
        }
    }

    //월별 시세
//    @UseCache(cacheKey = "id", ttl = 2L,unit = TimeUnit.HOURS,timeData = true)
    public List<PriceInfoDto> monthlyPrice(PriceInfoRequestDto priceInfoRequestDto, int id) throws ParseException {
        List<String> setDateList;
        List<String> setPriceList;
        List<PriceInfoDto> monthlyPriceList = new ArrayList<>();

        PriceApiRequestVariableDto var = new PriceApiRequestVariableDto(priceInfoRequestDto);

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=monthlySalesList&p_yyyy=" + var.getNowYear() + "&p_period=3&p_itemcategorycode=" + var.getCategoryCode() + "&p_itemcode=" + var.getItemCode() + "&p_kindcode=" + var.getKindCode() + "&p_graderank=" + var.getGradeRank() + "&p_countycode=" + var.getCountryCode() + "&p_convert_kg_yn=Y&p_cert_key=" + apiKey + "&p_cert_id=" + certId + "&p_returntype=json"); //URL
        try {
            JSONObject obj = openApiService.ApiCall(apiURL);
            JSONArray parse_price = (JSONArray) obj.get("price");
            // 도소매 데이터를 위해 반복
            for (int i = 0; i < parse_price.size(); i++) {
                JSONObject parse_date = (JSONObject) parse_price.get(i);
                String clsCode = (parse_date.get("productclscode").toString().equals("01")) ? "소매" : "도매";
                String[] stringa = parse_date.get("caption").toString().split(" > ");
                String unit = stringa[5];
                if (unit.replaceAll("[^\\d]", "").equals("1")) {
                    unit = unit.substring(1);
                }
                // 월별 시세데이터 리스트
                JSONArray monthPriceOfThreeYear = (JSONArray) parse_date.get("item");
                // 날짜 및 시세 데이터
                if (monthPriceOfThreeYear == null) {
                    List<String> list = Collections.emptyList();
                    setDateList = list;
                    setPriceList = list;
                } else {
                    List<String> priceList = priceService.monthlyPriceList(monthPriceOfThreeYear,priceInfoRequestDto.getYear(), priceInfoRequestDto.getMonth());
                    setDateList = priceService.makeDateList("month");
                    setPriceList = priceList;
                }
                PriceInfoDto monthPriceInfoDto = new PriceInfoDto(priceInfoRequestDto, var.getCountryCode(), clsCode, stringa, unit, setDateList, setPriceList);
                monthlyPriceList.add(monthPriceInfoDto);
            }
        } catch (IOException | NullPointerException | ClassCastException | IndexOutOfBoundsException e) {
            for (int i = 0; i < 2; i++) {
                PriceInfoDto monthPriceInfoDto = new PriceInfoDto(priceInfoRequestDto, var.getItemCode(), var.getCountryCode(), i);
                monthlyPriceList.add(monthPriceInfoDto);
            }
        }
        return monthlyPriceList;
    }

    //연도별 시세
    //    @UseCache(cacheKey = "id", ttl = 2L,unit = TimeUnit.HOURS,timeData = true)
    public List<PriceInfoDto> yearlyPrice(PriceInfoRequestDto priceInfoRequestDto, int id) throws ParseException {
        List<String> setDateList;
        List<String> setPriceList;
        List<PriceInfoDto> yearlyPriceList = new ArrayList<>();

        PriceApiRequestVariableDto var = new PriceApiRequestVariableDto(priceInfoRequestDto);

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=yearlySalesList&p_yyyy="+var.getNowYear()+"&p_itemcategorycode="+var.getCategoryCode()+"&p_itemcode="+var.getItemCode()+"&p_kindcode="+var.getKindCode()+"&p_graderank="+var.getGradeRank()+"&p_countycode="+var.getCountryCode()+"&p_convert_kg_yn=Y&p_cert_key="+apiKey+"&p_cert_id="+certId+"&p_returntype=json"); //URL
        
        try {
            JSONObject obj = openApiService.ApiCall(apiURL);
            JSONArray parse_price = (JSONArray) obj.get("price");
            List<String> dateList = priceService.makeDateList("year");
            // 도소매
            for (int i = 0; i < parse_price.size(); i++) {
                List<String[]> sumDataList = new ArrayList<>();
                List<String> yearPriceList = new ArrayList<>();
                JSONObject parse_date = (JSONObject) parse_price.get(i);
                String clsCode = (parse_date.get("productclscode").toString().equals("01")) ? "소매" : "도매";
                //단위
                String[] stringa = parse_date.get("caption").toString().split(" > ");
                String unit = stringa[5];
                if (unit.replaceAll("[^\\d]", "").equals("1")) {
                    unit = unit.substring(1);
                }
                // 연도별 시세
                if (parse_date.get("item").getClass().getSimpleName().equals("JSONArray")) {
                    JSONArray yearlyPrice = (JSONArray) parse_date.get("item");
                    priceService.getYearPrice(yearlyPrice, sumDataList);
                    //시세가 없는 연도는 0 할당
                    priceService.makeYearPrice(dateList, sumDataList, yearPriceList);

                    setDateList = dateList;
                    setPriceList = yearPriceList;
                } else {
                    List<String> list = Collections.emptyList();
                    setDateList = list;
                    setPriceList = list;
                }
                PriceInfoDto yearPriceInfoDto = new PriceInfoDto(priceInfoRequestDto, var.getCountryCode(), clsCode, stringa, unit, setDateList, setPriceList);
                yearlyPriceList.add(yearPriceInfoDto);
            }
        } catch (IOException | NullPointerException | ClassCastException | IndexOutOfBoundsException e) {
            for (int i = 0; i < 2; i++) {
                PriceInfoDto yearPriceInfoDto = new PriceInfoDto(priceInfoRequestDto, var.getItemCode(), var.getCountryCode(), i);
                yearlyPriceList.add(yearPriceInfoDto);
            }
        }
        return yearlyPriceList;
    }
}
