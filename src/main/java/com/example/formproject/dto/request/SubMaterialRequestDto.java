package com.example.formproject.dto.request;

import com.example.formproject.entity.SubMaterial;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class SubMaterialRequestDto {
    @Schema(type = "int", example = "0")
    private int type;
    @Schema(type = "String", example = "ABC 사 비료")
    private String product;
    @Schema(type = "String", example = "10포대")
    private String use;

    public SubMaterial build() {
        return SubMaterial.builder()
                .type(this.type)
                .product(this.product)
                .use(this.use)
                .build();
    }
}