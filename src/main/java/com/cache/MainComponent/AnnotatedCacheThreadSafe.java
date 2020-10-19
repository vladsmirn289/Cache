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
    private final Map<Key<String>, Future<Object>> cache = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<Key<String>> lruQueue = new PriorityBlockingQueue<>();
    private final CacheConfig cacheConfig;

    public AnnotatedCacheThreadSafe(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    @Around("@annotation(com.cache.MainComponent.CacheConcurrently)")
    public Object cacheMethod(ProceedingJoinPoint joinPoint) throws InterruptedException {
        int cacheSize = cacheConfig.getCacheSize();
        String methodName = joinPoint.getSignature().getName() + "(" + Arrays.toString(joinPoint.getArgs()) + ")";
        Key<String> key = new Key<>(methodName);

        Future<Object> future = cache.get(key);
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

            synchronized (this) {
                if (cacheSize > 0 && cache.size() == cacheSize) {
                    Key<String> oldestCache = lruQueue.take();
                    cache.remove(oldestCache);
                }
                future = cache.putIfAbsent(key, task);
            }

            if (future == null) {
                future = task;
                new Thread(task).start();
            } else {
                lruQueue.remove(key);
            }
            lruQueue.put(key);
        }

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (CancellationException e) {
            cache.remove(key, future);
        }

        return null;
    }
}
