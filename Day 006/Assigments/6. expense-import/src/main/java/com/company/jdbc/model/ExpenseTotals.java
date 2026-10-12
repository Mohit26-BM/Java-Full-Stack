package com.company.jdbc.model;

import java.math.BigDecimal;

/*
Row count and amount total of the whole expenses table.
Taken before and after an import to prove exactly the
valid rows were added.
*/
public class ExpenseTotals {

    private final long rowCount;
    private final BigDecimal totalAmount;

    public ExpenseTotals(long rowCount, BigDecimal totalAmount) {
        this.rowCount = rowCount;
        this.totalAmount = totalAmount;
    }

    public long getRowCount() {
        return rowCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
