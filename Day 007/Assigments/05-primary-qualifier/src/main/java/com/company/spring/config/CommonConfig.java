package com.company.spring.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.company.spring.repository.PaymentRepository;
import com.mysql.cj.jdbc.MysqlDataSource;

/*
Infrastructure shared by every scenario: DataSource + PaymentRepository.
Each scenario configuration @Imports this class.
*/
@Configuration
@PropertySource("classpath:db.properties")
public class CommonConfig {

    @Bean
    public DataSource dataSource(Environment environment) {

        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setURL(environment.getRequiredProperty("db.url"));
        dataSource.setUser(environment.getRequiredProperty("db.user"));
        dataSource.setPassword(environment.getRequiredProperty("db.password"));

        return dataSource;
    }

    @Bean
    public PaymentRepository paymentRepository(DataSource dataSource) {
        return new PaymentRepository(dataSource);
    }
}
