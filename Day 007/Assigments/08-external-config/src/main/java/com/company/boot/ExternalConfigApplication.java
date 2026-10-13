package com.company.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.company.boot.config.AppProperties;

/**
 * Entry point.
 *
 * @EnableConfigurationProperties registers AppProperties as a bean and binds
 * every "app.*" property to it.
 */
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class ExternalConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExternalConfigApplication.class, args);
    }
}
