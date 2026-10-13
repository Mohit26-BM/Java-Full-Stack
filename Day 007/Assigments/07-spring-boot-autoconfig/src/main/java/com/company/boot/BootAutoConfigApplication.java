package com.company.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
The ENTIRE configuration of this application.

@SpringBootApplication = three annotations in one:

  @SpringBootConfiguration  - this class is a @Configuration (a bean source)
  @EnableAutoConfiguration  - look at the classpath and properties, and
                              create the beans a typical app needs
                              (embedded Tomcat, Spring MVC, Jackson,
                              DataSource, JdbcTemplate, ...)
  @ComponentScan            - scan THIS package (com.company.boot) and
                              all sub-packages for @Component/@Service/
                              @RestController classes

SpringApplication.run(...) creates the ApplicationContext, runs the
auto-configuration and STARTS THE EMBEDDED TOMCAT SERVER - we never
configure or start a server ourselves.
*/
@SpringBootApplication
public class BootAutoConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootAutoConfigApplication.class, args);
    }
}
