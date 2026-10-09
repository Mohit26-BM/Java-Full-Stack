package com.example.flightcrud.config;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Bean
    public SessionFactory sessionFactory() {

        org.hibernate.cfg.Configuration configuration =    new org.hibernate.cfg.Configuration();

        Properties properties = new Properties();

        // Database connection
        properties.put(
                Environment.DRIVER,
                "com.mysql.cj.jdbc.Driver");

        properties.put(
                Environment.URL,
                "jdbc:mysql://localhost:3306/flights"
                        + "?createDatabaseIfNotExist=true"
                        + "&useSSL=false"
                        + "&serverTimezone=UTC");

        properties.put(
                Environment.USER,
                "root");

        properties.put(
                Environment.PASS,
                "mohit123");

        // Hibernate configuration
        properties.put(
                Environment.DIALECT,
                "org.hibernate.dialect.MySQLDialect");

        properties.put(
                Environment.HBM2DDL_AUTO,
                "update");

        properties.put(
                Environment.SHOW_SQL,
                "true");

        properties.put(
                Environment.FORMAT_SQL,
                "true");

        // Apply all properties
        configuration.setProperties(properties);

        // Register entity classes
        configuration.addAnnotatedClass(
                com.example.flightcrud.entity.Flight.class);
        //configuration.addAnnotatedClass(getClass());

        return configuration.buildSessionFactory();
    }
}
