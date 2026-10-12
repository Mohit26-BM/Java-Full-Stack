package com.company.jdbc.service;

import java.math.BigDecimal;
import java.util.List;

import com.company.jdbc.exception.EmployeeNotFoundException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Employee;
import com.company.jdbc.model.RatingBand;
import com.company.jdbc.model.ReviewSummary;

public interface PerformanceService {

    List<Employee> getAllEmployees();

    /*
    Calls the stored procedure for one employee.
    Works for employees with no reviews (count 0, band NO_REVIEWS).
    */
    ReviewSummary getReviewSummary(int employeeId)
            throws EmployeeNotFoundException;

    /*
    Calls the stored procedure once per employee.
    */
    List<ReviewSummary> getAllReviewSummaries();

    long addReview(int employeeId,
                   String reviewPeriod,
                   BigDecimal rating,
                   String reviewer,
                   String comments)
            throws EmployeeNotFoundException,
                   InvalidInputException;

    List<RatingBand> getRatingBands();

    void updateRatingBand(String bandName, BigDecimal minRating)
            throws InvalidInputException;
}
