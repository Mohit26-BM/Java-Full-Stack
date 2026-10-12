package com.company.jdbc.dao;

/*
Column names cannot be passed as PreparedStatement parameters,
so ORDER BY uses this fixed list instead of user text.
That keeps the query safe from SQL injection.
*/
public enum EmployeeSortField {

    NAME("employee_name"),
    JOINING_DATE("joining_date");

    private final String columnName;

    EmployeeSortField(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
