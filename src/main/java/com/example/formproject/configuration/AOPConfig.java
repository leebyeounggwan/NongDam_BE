package com.example.formproject.configuration;


import com.example.formproject.annotation.UseCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;

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
        log.info("["+ip+"] " + requestUri);
    }
    @Around("@annotation(com.example.formproject.annotation.UseCache)")
    public Object useCache(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getStaticPart().getSignature();
        UseCache annotation = signature.getMethod().getAnnotation(UseCache.class);
        String keyArg =  annotation.cacheKey();
        String cacheKey = signature.getMethod().getReturnType().getName()+":"+getCacheKeyArg(keyArg,joinPoint,signature).toString();

        if(template.hasKey(cacheKey)){
            System.out.println("Redis Cache 조회");
            return template.opsForValue().get(cacheKey);
        }else{
            Object o = joinPoint.proceed();
            BoundValueOperations<String,Object> saveObject = template.boundValueOps(cacheKey);
            saveObject.expire(Duration.ofHours(annotation.ttlHour()));
            saveObject.set(o);
            return o;
        }
    }

    public Object getCacheKeyArg(String keyArg,ProceedingJoinPoint joinPoint,MethodSignature signature){
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
