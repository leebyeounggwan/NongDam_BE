package com.example.formproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WeatherDto {
    // 일간/주간 시간
    @Schema(type = "List<Long>",example = "[1656993600, 1656993600, 1656993600, 1656993600, 1656993600, 1656993600]")
    private final List<Long> time;
    // 일간/주간 기온
    @Schema(type = "List<String>",example = "[23, 22, 23, 24, 23, 21]")
    private final List<String> temp;
    // 일간/주간 강수확률
    @Schema(type = "List<String>",example = "[12, 0, 100, 70, 45, 20]")
    private final List<String> pop;

    public WeatherDto(JSONObject obj, String select) {
        List<Long> TimeList = new ArrayList<>();
        List<String> TempList = new ArrayList<>();
        List<String> PopList = new ArrayList<>();

        JSONArray tempArr = (JSONArray) obj.get(select);
        if (select.equals("hourly")) {
            for(int i=1; i<17; i+=3) {
                JSONObject tempObj = (JSONObject)tempArr.get(i);
                setDataList(TimeList, TempList, PopList, tempObj, select, tempObj);
            }
        }
        if (select.equals("daily")) {
            for(int i=1; i<7; i++) {
                JSONObject tempObj = (JSONObject)tempArr.get(i);
                setDataList(TimeList, TempList, PopList, tempObj, select, tempObj);
            }
        }
        this.time = TimeList;
        this.temp = TempList;
        this.pop = PopList;
    }

    private void setDataList(List<Long> TimeList, List<String> TempList, List<String> PopList, JSONObject hourObj, String select, JSONObject tempObj) {
        Long time = (Long) tempObj.get("dt");

        TimeList.add(time);

        if (select.equals("hourly")) {
            TempList.add(tempObj.get("temp").toString().split("\\.")[0]);
        }
        if (select.equals("daily")) {
            JSONObject dayTemp = (JSONObject) tempObj.get("temp");
            TempList.add(dayTemp.get("day").toString().split("\\.")[0]);
        }

        if (hourObj.get("pop").toString().equals("0")) {
            PopList.add("0");
        } else if (hourObj.get("pop").toString().equals("1")) {
            PopList.add("100");
        }
        else {
            double pop = (double) hourObj.get("pop") * 100;
            int iPop = (int) pop;
            PopList.add(Integer.toString(iPop));
        }
    }


}
