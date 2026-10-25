package com.company.spring.edgecase;

import com.company.spring.service.CounterService;

/*
EDGE CASE: a SINGLETON bean that depends on a PROTOTYPE bean.

The prototype is injected ONCE, when ReportService is created.
After that, ReportService keeps using that same object forever -
"prototype" does NOT mean "new object on every method call".
*/
public class ReportService {

    private final CounterService counterService;

    public ReportService(CounterService counterService) {
        this.counterService = counterService;
    }

    public void generateReport() {
        System.out.println("   ReportService uses " + counterService
                + " -> count now " + counterService.increment());
    }
}
