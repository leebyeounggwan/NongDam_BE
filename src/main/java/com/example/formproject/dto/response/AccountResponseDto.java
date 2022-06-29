package com.example.formproject.dto.response;

import com.example.formproject.FinalValue;
import com.example.formproject.entity.AccountBook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AccountResponseDto {
    private long id;
    private int type;
    private int price;
    private String memo;
    private String date;
    private String category;

    public AccountResponseDto(AccountBook book){
        this.id = book.getId();
        this.type = book.getType();
        this.price = book.getPrice();
        this.memo = book.getMemo();
        this.date = book.getDate().format(FinalValue.DAY_FORMATTER);
        if(type > 2)
            this.category="지출";
        else
            this.category="수입";
    }

}
