package com.example.formproject;

import java.time.format.DateTimeFormatter;

public class FinalValue {
    public static final String LOGIN_URL = "/member/login";
    public final static String REDIRECT_URL = "http://localhost:3000/code/auth";

    public final static String APPLICATION_TITLE = "Form Project";

    public final static DateTimeFormatter DAYTIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public final static DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
