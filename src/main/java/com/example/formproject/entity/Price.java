package com.example.formproject.entity;

import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Getter
@Service
public class Price {
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
    // 일별/월별/연도별 날짜 리스트 생성
    public List<String> makeDateList(String select) {
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
                String date = sdf.format(cal.getTime()).split("-")[0]+"."+sdf.format(cal.getTime()).split("-")[1];

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

}
