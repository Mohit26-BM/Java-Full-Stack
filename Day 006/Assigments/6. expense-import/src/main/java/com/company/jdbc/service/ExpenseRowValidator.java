package com.company.jdbc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.company.jdbc.model.Employee;
import com.company.jdbc.model.Expense;
import com.company.jdbc.model.ExpenseCategory;

/*
Validates ONE CSV line and turns it into an Expense.

CSV format (one expense per line):
    employee_code,expense_date,category,amount,description

- expense_date must be yyyy-MM-dd
- description is optional and MAY contain commas, because the
  line is split into at most 5 parts (everything after the 4th
  comma belongs to the description)
- quoted CSV values ("...") are NOT supported

ALL problems of a row are collected, so the report shows every
reason at once instead of only the first one.
*/
class ExpenseRowValidator {

    /*
    "uuuu" + STRICT rejects impossible dates such as 2026-02-30.
    */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);

    /*
    Largest value that fits in DECIMAL(12,2).
    */
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999999999.99");

    private static final int MAX_DESCRIPTION_LENGTH = 255;

    private final Map<String, Employee> employeesByCode;
    private final LocalDate today;

    ExpenseRowValidator(Map<String, Employee> employeesByCode, LocalDate today) {
        this.employeesByCode = employeesByCode;
        this.today = today;
    }

    /*
    Returns the Expense when the line is valid.
    Returns null and adds reasons to 'problems' when it is not.
    */
    Expense validate(String line, List<String> problems) {

        String[] parts = line.split(",", 5);

        if (parts.length < 4) {
            problems.add("Expected at least 4 columns "
                    + "(employee_code,expense_date,category,amount[,description]) "
                    + "but found " + parts.length);
            return null;
        }

        Employee employee = validateEmployee(parts[0].trim(), problems);
        LocalDate date = validateDate(parts[1].trim(), problems);
        ExpenseCategory category = validateCategory(parts[2].trim(), problems);
        BigDecimal amount = validateAmount(parts[3].trim(), problems);
        String description = parts.length == 5
                ? validateDescription(parts[4].trim(), problems)
                : null;

        if (!problems.isEmpty()) {
            return null;
        }

        return new Expense(
                employee.getEmployeeId(),
                employee.getEmployeeCode(),
                date,
                category,
                amount,
                description
        );
    }

    private Employee validateEmployee(String code, List<String> problems) {

        if (code.isEmpty()) {
            problems.add("Employee code is blank");
            return null;
        }

        Employee employee = employeesByCode.get(code.toUpperCase());

        if (employee == null) {
            problems.add("Unknown employee code '" + code + "'");
        }

        return employee;
    }

    private LocalDate validateDate(String text, List<String> problems) {

        if (text.isEmpty()) {
            problems.add("Expense date is blank");
            return null;
        }

        try {

            LocalDate date = LocalDate.parse(text, DATE_FORMAT);

            if (date.isAfter(today)) {
                problems.add("Expense date " + text + " is in the future");
                return null;
            }

            return date;

        } catch (DateTimeParseException e) {

            problems.add("Invalid date '" + text + "' (expected a real date as yyyy-MM-dd)");
            return null;
        }
    }

    private ExpenseCategory validateCategory(String text, List<String> problems) {

        if (text.isEmpty()) {
            problems.add("Category is blank");
            return null;
        }

        try {

            return ExpenseCategory.valueOf(text.toUpperCase());

        } catch (IllegalArgumentException e) {

            problems.add("Category '" + text + "' is not allowed. Allowed: "
                    + Arrays.toString(ExpenseCategory.values()));
            return null;
        }
    }

    private BigDecimal validateAmount(String text, List<String> problems) {

        if (text.isEmpty()) {
            problems.add("Amount is blank");
            return null;
        }

        BigDecimal amount;

        try {
            amount = new BigDecimal(text);
        } catch (NumberFormatException e) {
            problems.add("Amount '" + text + "' is not a number");
            return null;
        }

        if (amount.signum() <= 0) {
            problems.add("Amount must be greater than 0 (found " + text + ")");
            return null;
        }

        if (amount.scale() > 2) {
            problems.add("Amount '" + text + "' has more than 2 decimal places");
            return null;
        }

        if (amount.compareTo(MAX_AMOUNT) > 0) {
            problems.add("Amount '" + text + "' is too large");
            return null;
        }

        return amount;
    }

    private String validateDescription(String text, List<String> problems) {

        if (text.isEmpty()) {
            return null;
        }

        if (text.length() > MAX_DESCRIPTION_LENGTH) {
            problems.add("Description is longer than "
                    + MAX_DESCRIPTION_LENGTH + " characters");
            return null;
        }

        return text;
    }
}
