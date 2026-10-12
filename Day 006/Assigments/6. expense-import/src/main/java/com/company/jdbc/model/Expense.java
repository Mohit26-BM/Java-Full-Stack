package com.company.jdbc.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/*
One expense row. employeeCode is kept for display and for
re-calculating report totals in Java (verification).
*/
public class Expense {

    private long expenseId;
    private int employeeId;
    private String employeeCode;
    private LocalDate expenseDate;
    private ExpenseCategory category;
    private BigDecimal amount;
    private String description;

    public Expense() {
    }

    public Expense(int employeeId,
                   String employeeCode,
                   LocalDate expenseDate,
                   ExpenseCategory category,
                   BigDecimal amount,
                   String description) {

        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.expenseDate = expenseDate;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(long expenseId) {
        this.expenseId = expenseId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
