package com.cache.MainComponent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;

@Component
@Aspect
public class AnnotatedCacheThreadSafe {
    private final Map<String, Future<Object>> cache = new ConcurrentHashMap<>();

    @Around("@annotation(com.cache.MainComponent.CacheConcurrently)")
    public Object cacheMethod(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName() + "(" + Arrays.toString(joinPoint.getArgs()) + ")";

        Future<Object> future = cache.get(methodName);
        if (future == null) {
            Callable<Object> callable = () -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    throw new CancellationException();
                }
            };
            FutureTask<Object> task = new FutureTask<>(callable);
            future = cache.putIfAbsent(methodName, task);
            if (future == null) {
                future = task;
                new Thread(task).start();
            }
        }

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (CancellationException e) {
            cache.remove(methodName, future);
        }

        return null;
    }
}
