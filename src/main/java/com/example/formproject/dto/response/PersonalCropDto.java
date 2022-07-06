package com.example.formproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalCropDto {
    @Schema(type = "PK",example = "1")
    private int id;
    @Schema(type = "String",example = "백미")
    private String name;
}
