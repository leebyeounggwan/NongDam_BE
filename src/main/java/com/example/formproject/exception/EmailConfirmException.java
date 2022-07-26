package com.example.formproject.exception;

public class EmailConfirmException extends CustomException {
    public EmailConfirmException(String m, String field) {
        super(m, field);
    }
}