package com.example.formproject.configuration;


import com.example.formproject.annotation.DeleteMemberCache;
import com.example.formproject.annotation.UseCache;
import com.example.formproject.dto.response.WeatherResponse;
import com.example.formproject.entity.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class AOPConfig {

    private final RedisTemplate<String, Object> template;

    @Pointcut("execution(* com.example.formproject.controller..*.*(..))")
    private void cut(){}

    @Before("cut()")
    public void LogBefore(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        String requestUri = request.getRequestURI();
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = request.getRemoteAddr();
        log.info("["+ip+"] "+request.getMethod()+" " + requestUri);
    }
    @Around("@annotation(com.example.formproject.annotation.UseCache)")
    public Object useCache(ProceedingJoinPoint joinPoint) throws Throwable {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        MethodSignature signature = (MethodSignature) joinPoint.getStaticPart().getSignature();
        UseCache annotation = signature.getMethod().getAnnotation(UseCache.class);
        String keyArg =  annotation.cacheKey();
        String cacheKey = signature.getMethod().getReturnType().getSimpleName()+":"+getCacheKeyArg(keyArg,joinPoint,signature).toString();
        if(template.hasKey(cacheKey)){
            if(annotation.timeData())
                return mapper.readValue(template.opsForValue().get(cacheKey).toString(),signature.getMethod().getReturnType());
            else
                return template.opsForValue().get(cacheKey);
        }else{
            Object o = joinPoint.proceed();
            BoundValueOperations<String,Object> saveObject = template.boundValueOps(cacheKey);
            if(annotation.timeData())
                saveObject.set(mapper.writer().writeValueAsString(o));
            else
                saveObject.set(o);
            if(annotation.ttl() == 0L){
                LocalDateTime expireTime = LocalDate.now().atTime(LocalDateTime.now().getHour()+1,0,0);
                long minute = Duration.between(LocalDateTime.now(),expireTime).toMinutes();
                template.expire(cacheKey,minute,annotation.unit());
            }else if(annotation.ttl()== -1L){
                LocalDateTime expireTime = LocalDate.now().atTime(16,0,0);
                if(expireTime.isBefore(LocalDateTime.now()))
                    expireTime.plusDays(1L);
                long minute = Duration.between(LocalDateTime.now(),expireTime).toMinutes();
                template.expire(cacheKey,minute,annotation.unit());
            }else{
                template.expire(cacheKey,annotation.ttl(),annotation.unit());
            }
            return o;
        }
    }
    @AfterReturning("@annotation(com.example.formproject.annotation.DeleteMemberCache)")
    public void deleteMember(JoinPoint joinPoint) throws Throwable {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        MethodSignature signature = (MethodSignature) joinPoint.getStaticPart().getSignature();
        DeleteMemberCache annotation = signature.getMethod().getAnnotation(DeleteMemberCache.class);
        String keyArg =  annotation.memberIdArg();
        String key = getCacheKeyArg(keyArg,joinPoint,signature).toString();
        String cacheKey = Member.class.getSimpleName()+":"+ key;
        if(template.hasKey(cacheKey)){
            template.delete(cacheKey);
        }
        cacheKey = WeatherResponse.class.getSimpleName()+":"+ key;
        if(template.hasKey(cacheKey)){
            template.delete(cacheKey);
        }
    }
    public Object getCacheKeyArg(String keyArg,JoinPoint joinPoint,MethodSignature signature){
        String[] argNames = signature.getParameterNames();
        int idx = -1;
        for(int i = 0; i < argNames.length; i++){
            if(argNames[i].equals(keyArg)){
                idx = i;
                break;
            }
        }
        if(idx == -1)
            return null;
        return joinPoint.getArgs()[idx];
    }
}
