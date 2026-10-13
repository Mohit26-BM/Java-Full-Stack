package com.company.spring.edgecase;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.company.spring.service.CounterService;

/*
CounterService is a prototype; ReportService and FreshReportService
are singletons (default scope) that use it in two different ways.
*/
@Configuration
public class EdgeCaseConfig {

    @Bean
    @Scope("prototype")
    public CounterService counterService() {
        return new CounterService();
    }

    @Bean
    public ReportService reportService(CounterService counterService) {
        return new ReportService(counterService);
    }

    @Bean
    public FreshReportService freshReportService(ObjectProvider<CounterService> counterProvider) {
        return new FreshReportService(counterProvider);
    }
}
