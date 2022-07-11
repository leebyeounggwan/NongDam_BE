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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedHeaders("*")
                .exposedHeaders("Authorization","RefreshToken")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_XML);
        mediaTypes.add(MediaType.TEXT_XML);
        mediaTypes.add(new MediaType("application","xml"));
        mediaTypes.add(new MediaType("text","xml"));
        mediaTypes.add(new MediaType("application", "*+xml"));
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
