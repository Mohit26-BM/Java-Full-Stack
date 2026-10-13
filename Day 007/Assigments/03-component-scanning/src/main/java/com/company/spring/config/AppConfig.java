package com.company.spring.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.mysql.cj.jdbc.MysqlDataSource;

/*
@ComponentScan("com.company.spring.customer")
    Spring searches this package AND all its sub-packages
    (customer.repository, customer.service, customer.component)
    for classes annotated with @Component, @Service, @Repository
    or @Controller, and registers each one as a bean automatically.

    Classes OUTSIDE this package (e.g. com.company.spring.outside)
    are never looked at.

@PropertySource
    loads db.properties into Spring's Environment, so the
    connection settings stay out of the code.

The DataSource is a library class (from the MySQL driver), so it
cannot carry @Component - it is registered with a @Bean method.
Rule of thumb: stereotypes for YOUR classes, @Bean for library objects.
*/
@Configuration
@ComponentScan(basePackages = "com.company.spring.customer")
@PropertySource("classpath:db.properties")
public class AppConfig {

    @Bean
    public DataSource dataSource(Environment environment) {

        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setURL(environment.getRequiredProperty("db.url"));
        dataSource.setUser(environment.getRequiredProperty("db.user"));
        dataSource.setPassword(environment.getRequiredProperty("db.password"));

        return dataSource;
    }
}
