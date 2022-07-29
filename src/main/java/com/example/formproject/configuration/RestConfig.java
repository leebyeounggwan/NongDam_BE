package com.example.formproject.configuration;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

    @Configuration
    public class RestConfig {
        @Bean
        RestTemplate restTemplate() {
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(5000);
            HttpClient httpClient = HttpClientBuilder.create() .setMaxConnTotal(100) .setMaxConnPerRoute(5) .build();
            factory.setHttpClient(httpClient);
            return new RestTemplate(factory);
        }
    }


