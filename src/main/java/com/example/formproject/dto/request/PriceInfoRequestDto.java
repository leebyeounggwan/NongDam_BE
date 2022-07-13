package com.example.formproject.dto.request;

import com.example.formproject.entity.Crop;
import com.example.formproject.security.MemberDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

import javax.persistence.Column;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PriceInfoRequestDto {
    private String productClsCode;
    private String gradeRank;
    private int category;
    private String data;
    private int type;
    private String kind;
    private String name;
    private String countryCode;
    private int year = new LocalDateTime().getYear();
    private int month = new LocalDateTime().getMonthOfYear();
    private int day = new LocalDateTime().getDayOfMonth();

    public PriceInfoRequestDto(Crop crop, PriceInfoRequestDto2 priceInfoRequestDto2, MemberDetail memberdetail) {

        this.productClsCode = priceInfoRequestDto2.getProductClsCode();
        this.gradeRank = priceInfoRequestDto2.getGradeRank();
        this.category = crop.getCategory();
        this.type = crop.getType();
        this.kind = crop.getKind();
        this.name = crop.getName();
        this.countryCode = memberdetail.getMember().getCountryCode()+"";
        this.data = priceInfoRequestDto2.getData();
    }
}

