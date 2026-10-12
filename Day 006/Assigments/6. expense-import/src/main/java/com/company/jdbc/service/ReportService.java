package com.company.jdbc.service;

import java.time.YearMonth;
import java.util.List;

import com.company.jdbc.model.MonthlyTotal;
import com.company.jdbc.model.ReconciliationResult;

public interface ReportService {

    /*
    month = null means all months.
    */
    List<MonthlyTotal> monthlyTotalsByEmployee(YearMonth month);

    List<MonthlyTotal> monthlyTotalsByCategory(YearMonth month);

    /*
    Acceptance test: SQL aggregation results must match the
    inserted data. Re-calculates every group in Java from the
    raw rows and compares count and total with the SQL reports.
    */
    ReconciliationResult verifyReports();
}
