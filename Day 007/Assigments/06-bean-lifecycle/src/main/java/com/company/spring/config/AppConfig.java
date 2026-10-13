package com.company.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/*
DataSource from DbConfig + DatabaseClient found by component scan.
*/
@Configuration
@Import(DbConfig.class)
@ComponentScan("com.company.spring.client")
public class AppConfig {
}
