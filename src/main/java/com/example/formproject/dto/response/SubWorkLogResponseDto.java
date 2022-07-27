package com.example.formproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubWorkLogResponseDto {
    private long id;
    private String title;
    private String date;
}
