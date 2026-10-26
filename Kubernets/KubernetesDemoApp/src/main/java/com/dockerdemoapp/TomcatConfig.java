package com.dockerdemoapp;

import org.springframework.boot.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public TomcatConnectorCustomizer maxLocaleCountCustomizer() {
        return connector -> connector.setMaxLocaleCount(50);
    }
}
