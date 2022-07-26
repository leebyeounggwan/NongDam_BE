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
    @UseCache(cacheKey = "cacheKey", ttl = 4L,unit = TimeUnit.HOURS,timeData = false)
    public DailyPriceResponseDto dailyPrice(PriceInfoRequestDto priceInfoRequestDto, String cacheKey) throws ParseException {

        List<String> dailyDate = makeDateList("day");
        PriceApiRequestVariableDto var = new PriceApiRequestVariableDto(priceInfoRequestDto, dailyDate);
        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=periodProductList&p_productclscode=" + var.getClsCode() + "&p_startday=" + var.getStartDay() + "&p_endday=" + var.getEndDay() + "&p_itemcategorycode=" + var.getCategoryCode() + "&p_itemcode=" + var.getItemCode() + "&p_kindcode=" + var.getKindCode() + "&p_productrankcode=" + var.getGradeRank() + "&p_countrycode=" + var.getCountryCode() + "&p_convert_kg_yn=Y&p_cert_key="+apiKey+"&p_cert_id="+certId+"&p_returntype=json"); /*URL*/

        try {
            JSONObject obj = openApiService.ApiCall(apiURL);
            if (obj.get("data").getClass().getSimpleName().equals("JSONObject")) {
                JSONObject parse_data = (JSONObject) obj.get("data");
                JSONArray parse_item = (JSONArray) parse_data.get("item");
                JSONObject parse_latestDate = new JSONObject();
                JSONObject parse_unit = (JSONObject) parse_item.get(parse_item.size()-1);

                for (int i = 0; i < parse_item.size(); i++) {
                    parse_latestDate = (JSONObject) parse_item.get(i);
                    if (parse_latestDate.get("countyname").equals("평년")) {
                        parse_latestDate = (JSONObject) parse_item.get(i-1);
                        break;
                    }
                }
                String unit = getUnit(parse_unit.get("kindname").toString().split("\\(")[1].replaceAll("\\)", ""));
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
    @UseCache(cacheKey = "cacheKey", ttl = 4L,unit = TimeUnit.HOURS,timeData = false)
    public List<PriceInfoDto> monthlyPrice(PriceInfoRequestDto priceInfoRequestDto, String cacheKey) throws ParseException {
        List<String> setDateList = makeDateList("month");
        List<PriceInfoDto> PriceList = new ArrayList<>();

        PriceApiRequestVariableDto var = new PriceApiRequestVariableDto(priceInfoRequestDto);

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=monthlySalesList&p_yyyy=" + var.getNowYear() + "&p_period=3&p_itemcategorycode=" + var.getCategoryCode() + "&p_itemcode=" + var.getItemCode() + "&p_kindcode=" + var.getKindCode() + "&p_graderank=" + var.getGradeRank() + "&p_countycode=" + var.getCountryCode() + "&p_convert_kg_yn=Y&p_cert_key=" + apiKey + "&p_cert_id=" + certId + "&p_returntype=json"); //URL
        try {
            JSONObject obj = openApiService.ApiCall(apiURL);
            JSONArray parse_price = new JSONArray();

            parse_price = checkType(obj, parse_price);

            mPriceSet(priceInfoRequestDto, setDateList, PriceList, var, parse_price);
        } catch (IOException | NullPointerException | ClassCastException | IndexOutOfBoundsException e) {
            for (int i = 0; i < 2; i++) {
                PriceInfoDto monthPriceInfoDto = new PriceInfoDto(priceInfoRequestDto, var.getItemCode(), var.getCountryCode(), i);
                PriceList.add(monthPriceInfoDto);
            }
        }
        return PriceList;
    }

    //연도별 시세
    @UseCache(cacheKey = "cacheKey", ttl = 4L,unit = TimeUnit.HOURS,timeData = false)
    public List<PriceInfoDto> yearlyPrice(PriceInfoRequestDto priceInfoRequestDto, String cacheKey) throws ParseException {
        List<String> setDateList = makeDateList("year");
        List<PriceInfoDto> yearlyPriceList = new ArrayList<>();

        PriceApiRequestVariableDto var = new PriceApiRequestVariableDto(priceInfoRequestDto);

        StringBuilder apiURL = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do?action=yearlySalesList&p_yyyy="+var.getNowYear()+"&p_itemcategorycode="+var.getCategoryCode()+"&p_itemcode="+var.getItemCode()+"&p_kindcode="+var.getKindCode()+"&p_graderank="+var.getGradeRank()+"&p_countycode="+var.getCountryCode()+"&p_convert_kg_yn=Y&p_cert_key="+apiKey+"&p_cert_id="+certId+"&p_returntype=json"); //URL
        
        try {
            JSONObject obj = openApiService.ApiCall(apiURL);
            JSONArray parse_price = new JSONArray();
            parse_price = checkType(obj, parse_price);
            // 도소매
            yPriceSet(priceInfoRequestDto, setDateList, yearlyPriceList, var, parse_price);

        } catch (IOException | NullPointerException | ClassCastException | IndexOutOfBoundsException e) {
            for (int i = 0; i < 2; i++) {
                PriceInfoDto yearPriceInfoDto = new PriceInfoDto(priceInfoRequestDto, var.getItemCode(), var.getCountryCode(), i);
                yearlyPriceList.add(yearPriceInfoDto);
            }
        }
        return yearlyPriceList;
    }

    // 연도별 시세 set
    private void yPriceSet(PriceInfoRequestDto priceInfoRequestDto, List<String> setDateList, List<PriceInfoDto> yearlyPriceList, PriceApiRequestVariableDto var, JSONArray parse_price) {
        List<String> setPriceList;
        for (int i = 0; i < parse_price.size(); i++) {
            List<String[]> sumDataList = new ArrayList<>();
            List<String> yearPriceList = new ArrayList<>();
            JSONObject parse_date = (JSONObject) parse_price.get(i);
            String clsCode = (parse_date.get("productclscode").toString().equals("01")) ? "소매" : "도매";
            String[] stringa = parse_date.get("caption").toString().split(" > ");
            String unit = getUnit(stringa[5]);
            // 연도별 시세
            if (parse_date.get("item").getClass().getSimpleName().equals("JSONArray")) {
                JSONArray yearlyPrice = (JSONArray) parse_date.get("item");
                getYearPrice(yearlyPrice, sumDataList);
                //시세가 없는 연도는 0 할당
                makeYearPrice(setDateList, sumDataList, yearPriceList);
                setPriceList = yearPriceList;
            } else {
                List<String> list = Collections.emptyList();
                setDateList = list;
                setPriceList = list;
            }
            aloneCase(priceInfoRequestDto, setDateList, yearlyPriceList, var, parse_price, setPriceList, clsCode, stringa, unit);
        }
    }

    // 월별 시세 데이터 set
    private void mPriceSet(PriceInfoRequestDto priceInfoRequestDto, List<String> setDateList, List<PriceInfoDto> PriceList, PriceApiRequestVariableDto var, JSONArray parse_price) {
        List<String> setPriceList;
        for (int i = 0; i < parse_price.size(); i++) {
            JSONObject parse_date = (JSONObject) parse_price.get(i);
            String clsCode = (parse_date.get("productclscode").toString().equals("01")) ? "소매" : "도매";
            String[] stringa = parse_date.get("caption").toString().split(" > ");
            String unit = getUnit(stringa[5]);
            // 월별 시세데이터 리스트
            JSONArray monthPriceOfThreeYear = (JSONArray) parse_date.get("item");
            // 날짜 및 시세 데이터
            if (monthPriceOfThreeYear == null) {
                List<String> list = Collections.emptyList();
                setPriceList = list;
            } else {
                List<String> priceList = monthlyPriceList(monthPriceOfThreeYear, priceInfoRequestDto.getYear(), priceInfoRequestDto.getMonth());
                setPriceList = priceList;
            }
            aloneCase(priceInfoRequestDto, setDateList, PriceList, var, parse_price, setPriceList, clsCode, stringa, unit);
        }
    }

    // 도,소매 둘중에 하나만 있는 경우
    private void aloneCase(PriceInfoRequestDto priceInfoRequestDto, List<String> setDateList, List<PriceInfoDto> PriceList, PriceApiRequestVariableDto var, JSONArray parse_price, List<String> setPriceList, String clsCode, String[] stringa, String unit) {
        PriceInfoDto PriceInfoDto = new PriceInfoDto(priceInfoRequestDto, var.getCountryCode(), clsCode, stringa, unit, setDateList, setPriceList);
        PriceList.add(PriceInfoDto);
        if (parse_price.size() == 1 && PriceInfoDto.getWholeSale().equals("도매")) {
            PriceInfoDto PriceInfoDto1 = new PriceInfoDto(priceInfoRequestDto, var.getItemCode(), var.getCountryCode(), 1);
            PriceList.add(PriceInfoDto1);
        }
        if (parse_price.size() == 1 && PriceInfoDto.getWholeSale().equals("소매")) {
            PriceInfoDto PriceInfoDto1 = new PriceInfoDto(priceInfoRequestDto, var.getItemCode(), var.getCountryCode(), 0);
            PriceList.add(PriceInfoDto1);
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

    // 단위 추출
    private String getUnit(String stringa) {
        String unit = stringa;
        if (unit.replaceAll("[^\\d]", "").equals("1")) {
            unit = unit.substring(1);
        }
        return unit;
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
                Collections.reverse(dateList);
                break;
            case "year":
                cal.add(Calendar.YEAR, 0);
                for (int i = 0; i < 6; i++) {
                    String date = sdf.format(cal.getTime()).split("-")[0];
                    cal.add(Calendar.YEAR, -1);
                    dateList.add(date);
                }
                Collections.reverse(dateList);
                break;
        }
        return dateList;
    }

    //연도별 시세 가져와서 리스트화
    public void getYearPrice(JSONArray yearlyPrice, List<String[]> sumDataList) {
        for (int j = 0; j < yearlyPrice.size(); j++) {
            JSONObject year = (JSONObject) yearlyPrice.get(j);
            if (!year.get("div").equals("평년")) {
                String[] dataList = new String[2];
                dataList[0] = year.get("div").toString();
                dataList[1] = year.get("avg_data").toString();
                sumDataList.add(dataList);
            }
        }
    }

    //연도별 시세 리스트 중 값이 없는 경우 "0"
    public void makeYearPrice(List<String> dateList, List<String[]> sumDataList, List<String> yearPriceList) {
        for (int k = 0; k < dateList.size(); k++) {
            boolean ok = true;
            for (int j = 0; j < sumDataList.size(); j++) {
                if (dateList.get(k).equals(sumDataList.get(j)[0])) {
                    yearPriceList.add(sumDataList.get(j)[1]);
                    ok = false;
                }
            }
            if (ok) {yearPriceList.add("0");}
        }
    }

    //월별 시세 2개월 단위로 추출
    public List<String> monthlyPriceList(JSONArray monthPriceOfThreeYear,int year, int month) {
        List<String> mPriceList = new ArrayList<>();
        String[] years = new String[] {(year-1)+"",year+""};
        List<String> zeroList = new ArrayList<>(Collections.nCopies(12, "0"));

        List<String> list = new ArrayList<>();
        for (String s : years) {
            for (int j = 0; j < monthPriceOfThreeYear.size(); j++) {
                JSONObject yearData = (JSONObject) monthPriceOfThreeYear.get(j);
                if (yearData.get("yyyy").equals(s)) {
                    for (int i = 1; i < yearData.size() - 1; i++) {
                        if (yearData.get("m" + i).toString().equals("-")) {
                            list.add("0");
                        } else {
                            list.add(yearData.get("m" + i).toString());
                        }
                    }
                    break;
                }
            }
            if (s.equals(years[0]) && list.isEmpty()){
                list.addAll(zeroList);
            }
            if (s.equals(years[1]) && list.size() == 12) {
                list.addAll(zeroList);
            }
        }
        for (int j = month + 11; j + 12 >= month + 11; j -= 2) {
            mPriceList.add(list.get(j));
        }
        Collections.reverse(mPriceList);
        return mPriceList;
    }
}
