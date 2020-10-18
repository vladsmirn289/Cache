package com.cache;

import com.cache.MainComponent.Cache;
import com.cache.MainComponent.CacheConcurrently;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Calculator {
    @Cache
    public long sum(long a, long b) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        return a+b;
    }

    @Cache
    public long subtract(long a, long b) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        return a-b;
    }

    @CacheConcurrently
    public long multiply(long a, long b) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        return a*b;
    }

    @CacheConcurrently
    public double divide(double a, double b) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        return a/b;
    }
}
