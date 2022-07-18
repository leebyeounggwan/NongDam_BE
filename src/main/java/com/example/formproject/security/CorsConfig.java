package com.example.formproject.security;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.utils.jackson.AdminServerModule;
import de.codecentric.boot.admin.server.utils.jackson.RegistrationDeserializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000","http://nongdamproject.s3-website.ap-northeast-2.amazonaws.com/","https://www.nongdam.site/","https://nongdam.site/")
                .allowedHeaders("*")
                .exposedHeaders("Authorization","RefreshToken")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }

    @Override
    public void configureContentNegotiation(
            ContentNegotiationConfigurer configurer) {
        final Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("charset", "utf-8");

        configurer.defaultContentType(new MediaType(
                MediaType.APPLICATION_JSON, parameterMap));
    }
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        Charset utf8 = Charset.forName("UTF-8");
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(new MediaType("application","json",utf8));
        mediaTypes.add(new MediaType("text","html",utf8));
        mediaTypes.add(new MediaType("application","xml",utf8));
        mediaTypes.add(new MediaType("text","xml",utf8));
        mediaTypes.add(new MediaType("application", "*+xml",utf8));
        mediaTypes.add(MediaType.ALL);
        jackson2HttpMessageConverter.setSupportedMediaTypes(mediaTypes);

        ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
//        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(Registration.class, ToStringSerializer.instance);
        simpleModule.addDeserializer(Registration.class, new RegistrationDeserializer());
        List<Module> modules = new ArrayList<>();
        modules.add(simpleModule);
        modules.add(new JavaTimeModule());
        modules.add(new AdminServerModule(new String[]{".*password$"}));
        objectMapper.registerModules(modules);
//        objectMapper.registerModule(simpleModule);
//        objectMapper.registerModule(new AdminServerModule(new String[]{".*passward$"}));
//        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.removeIf(v->v.getSupportedMediaTypes().contains(MediaType.APPLICATION_JSON));
        converters.add(jackson2HttpMessageConverter);
    }
}
