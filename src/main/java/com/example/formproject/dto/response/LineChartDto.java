package com.example.formproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LineChartDto {

    @Builder.Default
    @Schema(type = "List",example = "[\"2021-06\",\"2022-07\"]")
    private List<String> xLabel = new ArrayList<>();

    @Builder.Default
    private List<LineChartDataDto> datas = new ArrayList<>();

    public void addData(LineChartDataDto dto){
        this.datas.add(dto);
    }
    public void addLabel(String label){
        if(!xLabel.contains(label))
            this.xLabel.add(label);
    }
    public LineChartDataDto findDataByName(String name){
        LineChartDataDto dto = datas.stream().filter(e->e.getName().equals(name)).findFirst().orElse(null);
        if(dto == null){
            dto = LineChartDataDto.builder().name(name).build();
            this.datas.add(dto);
        }
        return dto;
    }


}
