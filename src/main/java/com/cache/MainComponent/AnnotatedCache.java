package com.cache.MainComponent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

@Component
@Aspect
public class AnnotatedCache {
    private final Map<Key<String>, Object> cache = new HashMap<>();
    private final PriorityQueue<Key<String>> lruQueue = new PriorityQueue<>();
    private final CacheConfig cacheConfig;

    public AnnotatedCache(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    @Around("@annotation(com.cache.MainComponent.Cache)")
    public Object cacheMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        int cacheSize = cacheConfig.getCacheSize();
        String methodName = methodSignature.getName() + "(" + Arrays.toString(joinPoint.getArgs()) + ")";
        Key<String> key = new Key<>(methodName);

        if (cache.get(key) == null) {
            Object result = joinPoint.proceed();

            if (cacheSize > 0 && cache.size() == cacheSize) {
                Key<String> oldestCache = lruQueue.remove();
                cache.remove(oldestCache);
            }

            lruQueue.add(key);
            cache.put(key, result);
            return result;
        }

        lruQueue.remove(key);
        Object result = cache.get(key);
        lruQueue.add(key);
        return result;
    }
}
