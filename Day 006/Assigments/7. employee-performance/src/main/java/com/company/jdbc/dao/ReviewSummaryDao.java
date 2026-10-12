package com.company.jdbc.dao;

import com.company.jdbc.model.ReviewSummary;

public interface ReviewSummaryDao {

    /*
    Calls sp_employee_review_summary(IN, OUT, OUT, OUT, OUT)
    with a CallableStatement and returns all OUT values plus the
    review rows returned by the procedure's result set.
    */
    ReviewSummary callReviewSummary(int employeeId);
}
