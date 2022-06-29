package com.example.formproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineChartDataDto {

    private String name;

    @Builder.Default
    private List<Long> data = new ArrayList<>();

    public void addData(long data){
        this.data.add(data);
    }
}
