package com.example.formproject;

import com.example.formproject.service.PriceInfoService;
import org.apache.http.HttpStatus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;

public class FinalValue {
    public static final String LOGIN_URL = "/member/login";
    public final static String REDIRECT_URL = "http://localhost:3000/code/auth";

    public final static String FRONT_URL = "https://nongdam.site";

    public final static String BACK_URL= "https://idontcare.shop";

    public final static String APPLICATION_TITLE = "농담 : 농사를 한눈에 담다.";

    public final static DateTimeFormatter DAYTIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public final static DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static String HTTPSTATUS_OK="200";
    public final static String HTTPSTATUS_FORBIDDEN="403";
    public final static String HTTPSTATUS_BADREQUEST="400";
    public final static String HTTPSTATUS_NOTFOUNT="404";
    public final static String HTTPSTATUS_SERVERERROR="500";
    public final static DateFormat PUBDATE_PARSSER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    public final static DateFormat PUBDATE_CONVERTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static int getBackDayOfWeekValue(DayOfWeek fromDay){
        return Math.abs(fromDay.getValue()-DayOfWeek.SATURDAY.getValue()+7)%7;
    }
    public static int getForwardDayOfWeekValue(DayOfWeek fromDay){
        return Math.abs((fromDay.getValue()==7?0:fromDay.getValue())-DayOfWeek.SATURDAY.getValue());
    }

}
