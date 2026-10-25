package com.company.spring.edgecase;

import org.springframework.beans.factory.ObjectProvider;

import com.company.spring.service.CounterService;

/*
FIX for the edge case: inject an ObjectProvider instead of the bean.

counterProvider.getObject() asks the container EACH time, so a
prototype really gives a new CounterService on every call.
*/
public class FreshReportService {

    private final ObjectProvider<CounterService> counterProvider;

    public FreshReportService(ObjectProvider<CounterService> counterProvider) {
        this.counterProvider = counterProvider;
    }

    public void generateReport() {

        CounterService counterService = counterProvider.getObject();

        System.out.println("   FreshReportService uses " + counterService
                + " -> count now " + counterService.increment());
    }
}
