package com.example.formproject.dto.request;

import com.example.formproject.FinalValue;
import com.example.formproject.entity.AccountBook;
import com.example.formproject.entity.Member;
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
    private int type;
    private int price;
    private String memo;
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
