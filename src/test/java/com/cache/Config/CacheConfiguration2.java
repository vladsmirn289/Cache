package com.cache.Config;

import com.cache.MainComponent.CacheConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CacheConfiguration2 {
    @Bean
    public CacheConfig cacheConfig2() {
        return new CacheConfig();
    }
}
