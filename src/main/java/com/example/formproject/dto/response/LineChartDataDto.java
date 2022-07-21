package com.example.formproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(type = "String",example = "수익")
    private String name;

    @Builder.Default
    @Schema(type = "List",example = "[100,200]")
    private List<Long> data = new ArrayList<>();

    public void addData(long data){
        this.data.add(data);
    }
}
