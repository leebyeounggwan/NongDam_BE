package com.example.formproject.service;

import com.example.formproject.annotation.UseCache;
import com.example.formproject.dto.response.NewsResponseDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NewsService {
    @Value("${news.naver.api.url}")
    private String apiUrl;

    @Value("${news.naver.api.client-id}")
    private String clientId;

    @Value("${news.naver.api.client-secret}")
    private String clientSecret;

    private String getNewsData(String query, int count){
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id",clientId);
        headers.set("X-Naver-Client-Secret",clientSecret);
        headers.set("Content-Type","application/json");
        HttpEntity entity = new HttpEntity(headers);
        String url = apiUrl+"?query="+query+"&display="+count;
        ResponseEntity<String> response = template.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
        String object = response.getBody();
        return object;
    }
//    private NewsResponseDto jsonToNewsResponseDto(JSONObject o) throws IOException, ParseException {
//        NewsResponseDto dto = new NewsResponseDto(o);
//        dto.setTime();
//        return dto;
//    }
    @UseCache(cacheKey = "tmp", ttl = 30L,unit= TimeUnit.MINUTES,timeData = false)
    public List<NewsResponseDto> getNewsInfo(String tmp) throws IOException, ParseException, org.json.simple.parser.ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(getNewsData("농사",10));
        JSONArray newsData = (JSONArray) object.get("items");
        List<NewsResponseDto> ret = new ArrayList<>();
        for(int i = 0; i < newsData.size(); i++){
            JSONObject o = (JSONObject) newsData.get(i);
            ret.add(new NewsResponseDto(o));
        }
        return ret;
    }
}
