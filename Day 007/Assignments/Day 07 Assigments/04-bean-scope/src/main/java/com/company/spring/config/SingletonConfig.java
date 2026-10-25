package com.company.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.spring.service.CounterService;

/*
No @Scope -> DEFAULT scope = SINGLETON.
One CounterService per container, created eagerly at startup
and shared by everyone who asks for it.
*/
@Configuration
public class SingletonConfig {

    @Bean
    public CounterService counterService() {
        return new CounterService();
    }
}
