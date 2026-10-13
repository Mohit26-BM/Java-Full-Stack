package com.company.spring.edgecase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.company.spring.service.CounterService;

/*
OPTIONAL EXTENSION / EDGE CASE: "request" scope outside a web application.

request and session scopes only exist inside a web application
context (Spring MVC / Spring Boot web), where the current HTTP
request/session is known. A plain AnnotationConfigApplicationContext
has no such scope registered, so asking for this bean fails.
*/
@Configuration
public class WebScopeConfig {

    @Bean
    @Scope("request")
    public CounterService requestCounter() {
        return new CounterService();
    }
}
