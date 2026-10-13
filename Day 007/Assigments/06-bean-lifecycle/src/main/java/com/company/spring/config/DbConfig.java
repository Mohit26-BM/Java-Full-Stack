package com.company.spring.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.mysql.cj.jdbc.MysqlDataSource;

/*
DataSource for the shared Day 07 database (settings in db.properties).
*/
@Configuration
@PropertySource("classpath:db.properties")
public class DbConfig {

    @Bean
    public DataSource dataSource(Environment environment) {

        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setURL(environment.getRequiredProperty("db.url"));
        dataSource.setUser(environment.getRequiredProperty("db.user"));
        dataSource.setPassword(environment.getRequiredProperty("db.password"));

        return dataSource;
    }
}
