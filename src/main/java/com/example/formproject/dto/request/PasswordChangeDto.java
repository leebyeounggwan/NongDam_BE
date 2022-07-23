package com.example.formproject.dto.request;

import lombok.Getter;

@Getter
public class PasswordChangeDto {
    private String oldPassword;
    private String newPassword;
}
