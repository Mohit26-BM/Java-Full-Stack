package com.company.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.company.boot.config.AppSettings;

/**
 * Entry point.
 *
 * The active profile is NOT chosen in code. It is chosen from outside, e.g.
 *   --spring.profiles.active=dev          (program argument)
 *   -Dspring.profiles.active=prod         (JVM argument)
 *   SPRING_PROFILES_ACTIVE=test           (environment variable)
 */
@SpringBootApplication
@EnableConfigurationProperties(AppSettings.class)
public class ProfilesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfilesApplication.class, args);
    }
}
