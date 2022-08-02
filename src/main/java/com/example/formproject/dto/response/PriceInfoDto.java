package com.example.formproject.dto.response;
import com.example.formproject.dto.request.PriceApiRequestVariableDto;
import com.example.formproject.dto.request.PriceInfoRequestDto;
import com.example.formproject.enums.CountryCode;
import com.example.formproject.enums.CropTypeCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public class PriceInfoDto {
    @Schema(type = "String",example = "쌀")
    private String crop;
    @Schema(type = "String",example = "백미")
    private String type;
    @Schema(type = "String",example = "kg")
    private String unit;
    @Schema(type = "String",example = "서울")
    private String country;
    @Schema(type = "String",example = "소매")
    private String wholeSale;
    @Schema(type = "List<String>",example = "[2021-07,2021-09,2021-11,2022-01,2022-03,2022-05,2022-07]")
    private List<String> dateList;
    @Schema(type = "List<String>",example = "[1,500,1,600,1,400,1,500,1,700,1,700,1,550]")
    private List<String> priceList;

    public PriceInfoDto(PriceInfoRequestDto RequestDto, PriceMetaDto meta, List<String> priceDateList, JSONObject parse_date) {
        List<String> priceList = new ArrayList<>();

        if(RequestDto.getData().equals("year")){
            priceList = setYearPrice(parse_date, priceList, priceDateList);
        }
        if(RequestDto.getData().equals("month")){
            priceList = setMonthPrice(parse_date, priceList, RequestDto);
        }

        this.crop = meta.getCrop();
        this.type = meta.getType();
        this.unit = meta.getUnit();
        this.country = meta.getCountry();
        this.wholeSale = meta.getWholeSale();
        this.dateList = priceDateList;
        this.priceList = priceList;
    }
    public PriceInfoDto(PriceMetaDto meta, int i) {
        List<String> list = Collections.emptyList();
        this.crop = meta.getCrop();
        this.type = meta.getType();
        this.unit = meta.getUnit();
        this.country = meta.getCountry();
        this.wholeSale = (i==0) ? "도매" : "소매";
        this.dateList = list;
        this.priceList = list;
    }

    //연도별 시세 가져와서 리스트화
    public List<String[]> getYearPrice(JSONArray yearlyPrice, List<String[]> sumDataList) {
        for (Object o : yearlyPrice) {
            JSONObject year = (JSONObject) o;
            if (!year.get("div").equals("평년")) {
                String[] dataList = new String[2];
                dataList[0] = year.get("div").toString();
                dataList[1] = year.get("avg_data").toString();
                sumDataList.add(dataList);
            }
        }
        return sumDataList;
    }

    //연도별 시세 리스트 중 값이 없는 경우 "0"
    public List<String> makeYearPrice(List<String> dateList, List<String[]> sumDataList, List<String> yearPriceList) {
        for (String s : dateList) {
            boolean ok = true;
            for (String[] strings : sumDataList) {
                if (s.equals(strings[0])) {
                    yearPriceList.add(strings[1]);
                    ok = false;
                }
            }
            if (ok) {
                yearPriceList.add("0");
            }
        }
        return yearPriceList;
    }

    // 연도별 시세
    public List<String> setYearPrice(JSONObject parse_date, List<String> yPriceList, List<String> priceDateList) {
        List<String[]> sumDataList = new ArrayList<>();
        if (parse_date.get("item").getClass().getSimpleName().equals("JSONArray")) {
            JSONArray yearlyPrice = (JSONArray) parse_date.get("item");
            sumDataList = getYearPrice(yearlyPrice, sumDataList);
            //시세가 없는 연도는 0 할당
            yPriceList = makeYearPrice(priceDateList, sumDataList, yPriceList);

        } else {
            List<String> list = Collections.emptyList();
            priceDateList = list;
            yPriceList = list;
        }
        return yPriceList;
    }

    // 월별 시세
    public List<String> setMonthPrice (JSONObject parse_date, List<String> mPriceList, PriceInfoRequestDto priceInfoRequestDto) {
        // 월별 시세데이터 리스트
        JSONArray monthPriceOfThreeYear = (JSONArray) parse_date.get("item");
        // 날짜 및 시세 데이터
        if (monthPriceOfThreeYear == null) {
            mPriceList = Collections.emptyList();
        } else {
            mPriceList = monthlyPriceList(monthPriceOfThreeYear, priceInfoRequestDto);
        }
        return mPriceList;
    }

    //월별 시세 2개월 단위로 추출
    public List<String> monthlyPriceList(JSONArray monthPriceOfThreeYear, PriceInfoRequestDto requestDto) {
        int year = requestDto.getYear();
        int month = requestDto.getMonth();
        List<String> mPriceList = new ArrayList<>();
        String[] years = new String[] {(year-1)+"",year+""};
        List<String> zeroList = new ArrayList<>(Collections.nCopies(12, "0"));

        List<String> list = new ArrayList<>();
        for (String s : years) {
            for (Object o : monthPriceOfThreeYear) {
                JSONObject yearData = (JSONObject) o;
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
