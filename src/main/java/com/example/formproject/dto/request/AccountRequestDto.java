package com.example.formproject.dto.request;

import com.example.formproject.FinalValue;
import com.example.formproject.entity.AccountBook;
import com.example.formproject.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDto {
    @Schema(type = "PK",example = "6")
    private int type;
    @Schema(type = "int",example = "1000")
    private int price;
    @Schema(type = "String",example = "비료구매")
    private String memo;
    @Schema(type = "String",example = "2022-07-06")
    private String date;
    public AccountBook build(Member member){
        return AccountBook.builder()
                .date(LocalDate.parse(date, FinalValue.DAY_FORMATTER))
                .type(this.type)
                .price(this.price)
                .memo(this.memo)
                .member(member)
                .build();
    }
}
