package com.company.spring.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.mysql.cj.jdbc.MysqlDataSource;

/*
EDGE CASE: @PostConstruct fails.

Same DatabaseClient, but the DataSource points to a database that
does not exist. Construction and injection succeed (no connection is
made yet); @PostConstruct then fails to connect, so the bean - and
the whole container - fails to start.
*/
@Configuration
@PropertySource("classpath:db.properties")
@ComponentScan("com.company.spring.client")
public class BrokenDbConfig {

    @Bean
    public DataSource dataSource(Environment environment) {

        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setURL("jdbc:mysql://localhost:3306/Day_007_Database_That_Does_Not_Exist");
        dataSource.setUser(environment.getRequiredProperty("db.user"));
        dataSource.setPassword(environment.getRequiredProperty("db.password"));

        return dataSource;
    }
}
