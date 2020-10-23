package com.cache.Config;

import com.cache.MainComponent.CacheConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CacheConfiguration3 {
    @Bean
    public CacheConfig cacheConfig() {
        CacheConfig cacheConfig = new CacheConfig();
        cacheConfig.setLifeTime(6);

        return cacheConfig;
    }
}
