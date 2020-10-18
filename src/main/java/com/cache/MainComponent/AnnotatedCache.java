package com.cache.MainComponent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
public class AnnotatedCache {
    private final Map<String, Object> cache = new HashMap<>();

    @Around("@annotation(com.cache.MainComponent.Cache)")
    public Object cacheMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName() + "(" + Arrays.toString(joinPoint.getArgs()) + ")";

        if (cache.get(methodName) == null) {
            Object result = joinPoint.proceed();
            cache.put(methodName, result);
            return result;
        }

        return cache.get(methodName);
    }
}
