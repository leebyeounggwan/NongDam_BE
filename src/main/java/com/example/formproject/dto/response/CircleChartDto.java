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
public class CircleChartDto {
    @Builder.Default
    @Schema(type = "List",example = "[\"수입\",\"지출\",\"순이익\"]")
    private List<String> labels= new ArrayList<>();
    @Builder.Default
    @Schema(type = "List",example = "[400,100,300]")
    private List<Long> data = new ArrayList<>();

    public void addLabel(String label){
        if(!this.labels.contains(label))
            labels.add(label);
    }
    public void addData(long data){
        this.data.add(data);
    }
}
