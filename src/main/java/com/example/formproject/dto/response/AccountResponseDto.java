package com.example.formproject.dto.response;

import com.example.formproject.FinalValue;
import com.example.formproject.entity.AccountBook;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AccountResponseDto {
    @Schema(type = "long",example = "1")
    private long id;
    @Schema(type = "int",example = "6")
    private int type;
    @Schema(type = "int",example = "1000")
    private int price;
    @Schema(type = "String",example = "비료구매")
    private String memo;
    @Schema(type = "String",example = "2022-07-06")
    private String date;
    @Schema(type = "String",example = "지출")
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
