package com.example.formproject.exception;

import lombok.Getter;

@Getter
public abstract class CustomException extends Exception {
    private String field;
    public CustomException(String m, String field){
        super(m);
        this.field =field;
    }
}
