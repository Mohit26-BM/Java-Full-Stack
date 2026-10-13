package com.company.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.company.spring.customer.repository.CustomerRepository;

/*
EDGE CASE: "CustomerRepository moved outside the scan package".

Same scan as AppConfig, but CustomerRepository is excluded - which
is exactly what Spring sees if the class is moved to a package that
is not scanned. CustomerService still needs it, so startup fails.
*/
@Configuration
@ComponentScan(
        basePackages = "com.company.spring.customer",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = CustomerRepository.class))
public class MissingRepositoryConfig {
}
