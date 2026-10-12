package com.company.jdbc.model;

import java.math.BigDecimal;
import java.util.List;

/*
Everything the import produced, for display and verification.
*/
public class ImportResult {

    private final int dataRowCount;
    private final List<Expense> validRows;
    private final List<ImportError> errors;
    private final List<int[]> batchUpdateCounts;
    private final ExpenseTotals totalsBefore;
    private final ExpenseTotals totalsAfter;

    public ImportResult(int dataRowCount,
                        List<Expense> validRows,
                        List<ImportError> errors,
                        List<int[]> batchUpdateCounts,
                        ExpenseTotals totalsBefore,
                        ExpenseTotals totalsAfter) {

        this.dataRowCount = dataRowCount;
        this.validRows = validRows;
        this.errors = errors;
        this.batchUpdateCounts = batchUpdateCounts;
        this.totalsBefore = totalsBefore;
        this.totalsAfter = totalsAfter;
    }

    /*
    Sum of the amounts of the valid rows (what SHOULD have been added).
    */
    public BigDecimal getValidAmountTotal() {

        BigDecimal total = BigDecimal.ZERO;

        for (Expense expense : validRows) {
            total = total.add(expense.getAmount());
        }

        return total;
    }

    public int getDataRowCount() {
        return dataRowCount;
    }

    public List<Expense> getValidRows() {
        return validRows;
    }

    public List<ImportError> getErrors() {
        return errors;
    }

    public List<int[]> getBatchUpdateCounts() {
        return batchUpdateCounts;
    }

    public ExpenseTotals getTotalsBefore() {
        return totalsBefore;
    }

    public ExpenseTotals getTotalsAfter() {
        return totalsAfter;
    }
}
