package com.example.formproject.dto.request;

import com.example.formproject.entity.Crop;
import com.example.formproject.security.MemberDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PriceInfoRequestDto {
    @Schema(type = "String",example = "소매")
    private String productClsCode;
    @Schema(type = "String",example = "상품")
    private String gradeRank;
    @Schema(type = "String",example = "month")
    private String data;
    @Schema(type = "int",example = "100")
    private int category;
    @Schema(type = "int",example = "111")
    private int type;
    @Schema(type = "String",example = "01")
    private String kind;
    @Schema(type = "String",example = "양배추")
    private String name;
    @Schema(type = "String",example = "1101")
    private String countryCode;

    LocalDateTime dateTime = new LocalDateTime();

    private int year = dateTime.getYear();
    private int month = dateTime.getMonthOfYear();
    private int day = dateTime.getDayOfMonth();

    public PriceInfoRequestDto(Crop crop, String data, MemberDetail memberdetail) {

        this.productClsCode = null;
        this.gradeRank = null;
        this.category = crop.getCategory();
        this.type = crop.getType();
        this.kind = crop.getKind();
        this.name = crop.getName();
        this.countryCode = memberdetail.getMember().getCountryCode()+"";
        this.data = data;
    }
    public PriceInfoRequestDto(Crop crop, MemberDetail memberdetail, String clsCode) {
        this.productClsCode = clsCode;
        this.gradeRank = null;
        this.category = crop.getCategory();
        this.type = crop.getType();
        this.kind = crop.getKind();
        this.name = crop.getName();
        this.countryCode = memberdetail.getMember().getCountryCode()+"";
        this.data = null;
    }
}

