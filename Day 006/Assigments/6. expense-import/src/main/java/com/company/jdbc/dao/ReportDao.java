package com.company.jdbc.dao;

import java.time.YearMonth;
import java.util.List;

import com.company.jdbc.model.MonthlyTotal;

public interface ReportDao {

    /*
    month = null means all months.
    */
    List<MonthlyTotal> monthlyTotalsByEmployee(YearMonth month);

    List<MonthlyTotal> monthlyTotalsByCategory(YearMonth month);
}
