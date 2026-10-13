package com.company.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.company.spring.service.CounterService;

/*
Same bean, @Scope("prototype"):
a NEW CounterService is created EVERY time one is requested
(getBean or injection). Nothing is created at startup.

The class-based form is the same annotation on the class:
    @Component
    @Scope("prototype")
    public class CounterService { ... }
*/
@Configuration
public class PrototypeConfig {

    @Bean
    @Scope("prototype")
    public CounterService counterService() {
        return new CounterService();
    }
}
