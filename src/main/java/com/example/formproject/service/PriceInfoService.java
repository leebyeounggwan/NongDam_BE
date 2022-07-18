package com.example.formproject.service;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.PriceApiRequestVariableDto;
import com.example.formproject.dto.request.PriceInfoRequestDto;
import com.example.formproject.dto.response.DailyPriceResponseDto;
import com.example.formproject.dto.response.PriceInfoDto;
import com.example.formproject.entity.Price;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class PriceInfoService {
    private final Price price;
    private final OpenApiService openApiService;
    @Value("e038dee1-a1d5-426d-ba4c-6bcd8c7100cb")
    private static String apiKey;
    @Value("lbk0622")
    private static String certId;

    //일별 시세
    public DailyPriceResponseDto dailyPrice(PriceInfoRequestDto priceInfoRequestDto) throws ParseException {

        List<String> dailyDate = price.makeDateList("day");
        PriceApiRequestVariableDto var = new PriceApiRequestVariableDto(priceInfoRequestDto, dailyDate);
        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=periodProductList&p_productclscode=" + var.getClsCode() + "&p_startday=" + var.getStartDay() + "&p_endday=" + var.getEndDay() + "&p_itemcategorycode=" + var.getCategoryCode() + "&p_itemcode=" + var.getItemCode() + "&p_kindcode=" + var.getKindCode() + "&p_productrankcode=" + var.getGradeRank() + "&p_countrycode=" + var.getCountryCode() + "&p_convert_kg_yn=Y&p_cert_key="+apiKey+"&p_cert_id="+certId+"&p_returntype=json"); /*URL*/

        try {
            JSONObject obj = openApiService.ApiCall(apiURL);

            if (obj.get("data").getClass().getSimpleName().equals("JSONObject")) {
                JSONObject parse_data = (JSONObject) obj.get("data");
                JSONArray parse_item = (JSONArray) parse_data.get("item");
                //데이터 중 가장 마지막 인덱스(가장 최근 조사된 시세)
                JSONObject parse_latestDate = (JSONObject) parse_item.get(parse_item.size() - 1);
                //단위
                String unit = parse_latestDate.get("kindname").toString().split("\\(")[1].replaceAll("\\)", "");
                if (!unit.contains("kg")) {
                    unit = "kg";
                } else if (unit.replaceAll("[^\\d]", "").equals("1")) {
                    unit = unit.substring(1);
                }
                return new DailyPriceResponseDto(priceInfoRequestDto, parse_latestDate, unit);
            } else {
                return new DailyPriceResponseDto(priceInfoRequestDto, var.getItemCode(), var.getCountryCode());
            }
        }
        catch (IOException | NullPointerException | ClassCastException | IndexOutOfBoundsException e) {
            return new DailyPriceResponseDto(priceInfoRequestDto, var.getItemCode(), var.getCountryCode());
        }
    }

    //월별 시세
    public List<PriceInfoDto> monthlyPrice(PriceInfoRequestDto priceInfoRequestDto) throws ParseException {
        List<String> setDateList;
        List<String> setPriceList;
        List<PriceInfoDto> monthlyPriceList = new ArrayList<>();
        int month = priceInfoRequestDto.getMonth();

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
                // 3년간의 데이터 리스트
                JSONArray monthPriceOfThreeYear = (JSONArray) parse_date.get("item");
                // 날짜 및 시세 데이터
                if (monthPriceOfThreeYear == null) {
                    List<String> list = Collections.emptyList();
                    setDateList = list;
                    setPriceList = list;
                } else {
                    List<String> priceList = monthlyPriceList(monthPriceOfThreeYear, month);
                    setDateList = price.makeDateList("month");
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
    public List<PriceInfoDto> yearlyPrice(PriceInfoRequestDto priceInfoRequestDto) throws ParseException {
        List<String> setDateList;
        List<String> setPriceList;
        List<PriceInfoDto> yearlyPriceList = new ArrayList<>();

        PriceApiRequestVariableDto var = new PriceApiRequestVariableDto(priceInfoRequestDto);

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=yearlySalesList&p_yyyy="+var.getNowYear()+"&p_itemcategorycode="+var.getCategoryCode()+"&p_itemcode="+var.getItemCode()+"&p_kindcode="+var.getKindCode()+"&p_graderank="+var.getGradeRank()+"&p_countycode="+var.getCountryCode()+"&p_convert_kg_yn=Y&p_cert_key="+apiKey+"&p_cert_id="+certId+"&p_returntype=json"); //URL
        
        try {
            JSONObject obj = openApiService.ApiCall(apiURL);
            JSONArray parse_price = (JSONArray) obj.get("price");
            List<String> dateList = price.makeDateList("year");
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
                    price.getYearPrice(yearlyPrice, sumDataList);
                    //시세가 없는 연도는 0 할당
                    price.makeYearPrice(dateList, sumDataList, yearPriceList);

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
    // 월별 연도별
    public List<PriceInfo> makeListV2(JSONArray a){
        List<PriceInfo> ret = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            JSONObject o = (JSONObject) a.get(i);
            int year = Integer.parseInt(o.get("yyyy").toString());
            for (int j = 1; j < o.size() - 1; j++) {
                String tmp =  o.get("m"+j).toString();
                String price = tmp.equals("-")?"0":tmp;
                ret.add(new PriceInfo(year,j,price));
            }
        }
        return ret;
    }

    //월별 시세에서 비어있는 데이터 채워넣고 2개월 단위로 추출
    public List<String> monthlyPriceList(JSONArray monthPriceOfThreeYear, int month) {
//        List<String> sumList = new ArrayList<>();
        List<PriceInfo> sumList = new ArrayList<>();
        List<String> mPriceList = new ArrayList<>();
        List<String> finalList = new ArrayList<>();

        JSONArray threeYears = new JSONArray();
        for (int j = 1; j < monthPriceOfThreeYear.size(); j++) {
            threeYears.add(monthPriceOfThreeYear.get(j));
        }

        sumList = makeListV2(threeYears);
        Collections.sort(sumList, FinalValue.PRICE_INFO_COMPARABLE);
        Collections.reverse(sumList);

        for(int l = 0; l < sumList.size();l++){
            finalList.add(sumList.get(l).getPrice());
            if(finalList.size() == 24)
                break;
        }
        for (int j = month + 11; j + 12 >= month + 11; j -= 2) {
            mPriceList.add(finalList.get(j));
        }
        return mPriceList;
    }

    @AllArgsConstructor
    @Getter
    public class PriceInfo{
        private int year;
        private int month;
        private String price;
    }
}
