package com.company.jdbc.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.company.jdbc.dao.ExpenseDao;
import com.company.jdbc.dao.ReportDao;
import com.company.jdbc.model.Expense;
import com.company.jdbc.model.MonthlyTotal;
import com.company.jdbc.model.ReconciliationResult;

public class ReportServiceImpl implements ReportService {

    /*
    Count and sum for one group, calculated in Java.
    */
    private static class GroupTotal {

        long count;
        BigDecimal sum = BigDecimal.ZERO;

        void add(BigDecimal amount) {
            count++;
            sum = sum.add(amount);
        }
    }

    private final ReportDao reportDao;
    private final ExpenseDao expenseDao;

    public ReportServiceImpl(ReportDao reportDao, ExpenseDao expenseDao) {
        this.reportDao = reportDao;
        this.expenseDao = expenseDao;
    }

    @Override
    public List<MonthlyTotal> monthlyTotalsByEmployee(YearMonth month) {
        return reportDao.monthlyTotalsByEmployee(month);
    }

    @Override
    public List<MonthlyTotal> monthlyTotalsByCategory(YearMonth month) {
        return reportDao.monthlyTotalsByCategory(month);
    }

    @Override
    public ReconciliationResult verifyReports() {

        List<Expense> rows = expenseDao.findAll();

        /*
        Java totals, key = "month|employee code" or "month|category".
        */
        Map<String, GroupTotal> javaByEmployee = new TreeMap<>();
        Map<String, GroupTotal> javaByCategory = new TreeMap<>();

        for (Expense expense : rows) {

            String month = YearMonth.from(expense.getExpenseDate()).toString();

            groupFor(javaByEmployee, month + "|" + expense.getEmployeeCode())
                    .add(expense.getAmount());

            groupFor(javaByCategory, month + "|" + expense.getCategory().name())
                    .add(expense.getAmount());
        }

        List<String> mismatches = new ArrayList<>();

        List<MonthlyTotal> sqlByEmployee = reportDao.monthlyTotalsByEmployee(null);
        List<MonthlyTotal> sqlByCategory = reportDao.monthlyTotalsByCategory(null);

        compare("By employee", sqlByEmployee, javaByEmployee, mismatches);
        compare("By category", sqlByCategory, javaByCategory, mismatches);

        return new ReconciliationResult(
                rows.size(),
                sqlByEmployee.size(),
                sqlByCategory.size(),
                mismatches
        );
    }

    private GroupTotal groupFor(Map<String, GroupTotal> totals, String key) {

        GroupTotal total = totals.get(key);

        if (total == null) {
            total = new GroupTotal();
            totals.put(key, total);
        }

        return total;
    }

    private void compare(String reportName,
                         List<MonthlyTotal> sqlRows,
                         Map<String, GroupTotal> javaTotals,
                         List<String> mismatches) {

        Map<String, GroupTotal> remaining = new TreeMap<>(javaTotals);

        for (MonthlyTotal row : sqlRows) {

            String key = row.getMonth() + "|" + row.getGroupKey();
            GroupTotal expected = remaining.remove(key);

            if (expected == null) {
                mismatches.add(reportName + " " + key + ": in SQL report but not in raw data");
                continue;
            }

            if (row.getExpenseCount() != expected.count
                    || row.getTotalAmount().compareTo(expected.sum) != 0) {

                mismatches.add(reportName + " " + key
                        + ": SQL " + row.getExpenseCount() + " / Rs. " + row.getTotalAmount()
                        + " vs Java " + expected.count + " / Rs. " + expected.sum);
            }
        }

        for (String key : remaining.keySet()) {
            mismatches.add(reportName + " " + key + ": in raw data but missing from SQL report");
        }
    }
}
