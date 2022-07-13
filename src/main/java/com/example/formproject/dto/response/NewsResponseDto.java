package com.example.formproject.dto.response;

import com.example.formproject.FinalValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponseDto implements Serializable {
    private String time;
    private String title;
    private String descript;
    private String link;
    private String imageUrl;
    private String pubDate;
    private String article;

    public NewsResponseDto(JSONObject object) throws IOException {
        this.title = object.get("title").toString().replaceAll("&quot;","");
        this.descript = object.get("description").toString().replaceAll("<b>","").replaceAll("</b>","");
        this.link = object.get("link").toString();
        this.pubDate = object.get("pubDate").toString();
        Connection conn = Jsoup.connect(this.link);
        Document document = conn.get();
        Element head = document.getElementsByTag("head").get(0);
        try {
            this.imageUrl = head.getElementsByAttributeValue("property", "og:image").get(0).attr("content");
        }catch (IndexOutOfBoundsException e){
            this.imageUrl = "";
        }
        try {
            this.article = head.getElementsByAttributeValue("property", "og:article:author").get(0).attr("content");
        }catch (IndexOutOfBoundsException e){
            try{
                head.getElementsByAttributeValue("property", "Copyright").get(0).attr("content");
            }catch (IndexOutOfBoundsException f){
                this.article="";
            }
        }
    }
    public void setTime() throws ParseException {
        this.time = convertTimeData(this.pubDate);
    }
    private String convertTimeData(String pubDate) throws ParseException {
        Date date = FinalValue.PUBDATE_PARSSER.parse(pubDate);
        LocalDateTime time = convertDateToLocal(date);

        Duration duration = Duration.between(time,LocalDateTime.now());
        if(duration.toDays() > 0){
            return duration.toDaysPart()+"일 전";
        }else if(duration.toHoursPart() > 0){
            return duration.toHoursPart()+"시간 전";
        }else if(duration.toMinutesPart() > 0){
            return duration.toMinutesPart() +"분 전";
        }else{
            return "방금 전";
        }

    }
    private LocalDateTime convertDateToLocal(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
