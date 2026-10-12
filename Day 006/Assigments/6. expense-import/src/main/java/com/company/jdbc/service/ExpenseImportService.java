package com.company.jdbc.service;

import java.util.List;

import com.company.jdbc.exception.ImportFailedException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Employee;
import com.company.jdbc.model.ImportResult;

public interface ExpenseImportService {

    /*
    filePath = null imports the bundled sample (resources/expenses.csv).
    */
    ImportResult importCsv(String filePath)
            throws InvalidInputException,
                   ImportFailedException;

    List<Employee> getEmployees();

    int getBatchSize();
}
