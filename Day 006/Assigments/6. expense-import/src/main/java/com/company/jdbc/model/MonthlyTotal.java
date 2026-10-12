package com.company.jdbc.model;

import java.math.BigDecimal;

/*
One row of a monthly report.

groupKey is the employee code (report by employee)
or the category name (report by category).
groupLabel is the employee name, or null for categories.
*/
public class MonthlyTotal {

    private final String month;
    private final String groupKey;
    private final String groupLabel;
    private final long expenseCount;
    private final BigDecimal totalAmount;

    public MonthlyTotal(String month,
                        String groupKey,
                        String groupLabel,
                        long expenseCount,
                        BigDecimal totalAmount) {

        this.month = month;
        this.groupKey = groupKey;
        this.groupLabel = groupLabel;
        this.expenseCount = expenseCount;
        this.totalAmount = totalAmount;
    }

    public String getMonth() {
        return month;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public long getExpenseCount() {
        return expenseCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
