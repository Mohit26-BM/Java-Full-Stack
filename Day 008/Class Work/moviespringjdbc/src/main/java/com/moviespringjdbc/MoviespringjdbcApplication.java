package com.moviespringjdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "controller",
        "service",
        "dao",
        "configs",
        "model"
})
public class MoviespringjdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviespringjdbcApplication.class, args);
    }
}