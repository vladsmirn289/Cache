package com.cache.MainComponent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@Component
@Aspect
public class AnnotatedCache {
    protected final CacheConfig cacheConfig;
    protected final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);

                return thread;
            }
    );
    private final Map<Key<String>, Future<Object>> cache = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<Key<String>> lruQueue = new PriorityBlockingQueue<>();
    protected int cacheSize;
    protected int lifeTime;

    public AnnotatedCache(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
        cacheSize = cacheConfig.getCacheSize();
        lifeTime = cacheConfig.getLifeTime();

        if (lifeTime > 0) {
            executorService.scheduleWithFixedDelay(() -> {
                cache.keySet().stream()
                        .peek(k -> System.out.println(LocalTime.now() + "; Key - " + k.getValue()))
                        .filter(key -> !key.isLive())
                        .forEach(key -> {
                            cache.remove(key);
                            lruQueue.remove(key);
                            System.out.println(LocalTime.now() + "; Key - " + key.getValue() + " deleted");
                        });
            }, 1, 1000, TimeUnit.MILLISECONDS);
        }
    }

    @Around("@annotation(com.cache.MainComponent.Cache)")
    public Object cacheMethod(ProceedingJoinPoint joinPoint) throws InterruptedException {
        System.out.println("Start method: " + LocalTime.now());

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

    protected class Key<T> implements Comparable<Key<T>> {
        private T value;
        private long creationTime = System.currentTimeMillis();

        public Key(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public long getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(long creationTime) {
            this.creationTime = creationTime;
        }

        public boolean isLive() {
            return (System.currentTimeMillis() - creationTime)/1000 < lifeTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key<?> key = (Key<?>) o;
            return Objects.equals(value, key.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public int compareTo(Key<T> o) {
            return (int)(creationTime/1000) - (int)(o.getCreationTime()/1000);
        }
    }
}
