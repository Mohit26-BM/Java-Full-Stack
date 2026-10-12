package com.company.jdbc.model;

import java.util.List;

/*
Result of comparing the SQL report totals with totals
re-calculated in Java from the raw expense rows.
*/
public class ReconciliationResult {

    private final int rawRowCount;
    private final int employeeGroupsChecked;
    private final int categoryGroupsChecked;
    private final List<String> mismatches;

    public ReconciliationResult(int rawRowCount,
                                int employeeGroupsChecked,
                                int categoryGroupsChecked,
                                List<String> mismatches) {

        this.rawRowCount = rawRowCount;
        this.employeeGroupsChecked = employeeGroupsChecked;
        this.categoryGroupsChecked = categoryGroupsChecked;
        this.mismatches = mismatches;
    }

    public int getRawRowCount() {
        return rawRowCount;
    }

    public int getEmployeeGroupsChecked() {
        return employeeGroupsChecked;
    }

    public int getCategoryGroupsChecked() {
        return categoryGroupsChecked;
    }

    public List<String> getMismatches() {
        return mismatches;
    }

    public boolean isMatching() {
        return mismatches.isEmpty();
    }
}
