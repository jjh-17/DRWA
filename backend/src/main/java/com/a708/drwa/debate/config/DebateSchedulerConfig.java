package com.a708.drwa.debate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Configuration
public class DebateSchedulerConfig {
    @Bean
    public Map<String, ScheduledFuture<?>> scheduledFutures() {
        return new ConcurrentHashMap<>();
    }
}
