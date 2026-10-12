package com.company.jdbc.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.dao.EmployeeDao;
import com.company.jdbc.dao.RatingBandDao;
import com.company.jdbc.dao.ReviewDao;
import com.company.jdbc.dao.ReviewSummaryDao;
import com.company.jdbc.exception.EmployeeNotFoundException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Employee;
import com.company.jdbc.model.PerformanceReview;
import com.company.jdbc.model.RatingBand;
import com.company.jdbc.model.ReviewSummary;

public class PerformanceServiceImpl implements PerformanceService {

    /*
    YYYY-Qn, e.g. 2026-Q2. This format sorts correctly as text,
    which the procedure relies on for MAX(review_period).
    */
    private static final String PERIOD_PATTERN = "\\d{4}-Q[1-4]";

    private static final BigDecimal MIN_RATING = new BigDecimal("1.00");
    private static final BigDecimal MAX_RATING = new BigDecimal("5.00");

    private final EmployeeDao employeeDao;
    private final ReviewDao reviewDao;
    private final ReviewSummaryDao reviewSummaryDao;
    private final RatingBandDao ratingBandDao;

    public PerformanceServiceImpl(EmployeeDao employeeDao,
                                  ReviewDao reviewDao,
                                  ReviewSummaryDao reviewSummaryDao,
                                  RatingBandDao ratingBandDao) {

        this.employeeDao = employeeDao;
        this.reviewDao = reviewDao;
        this.reviewSummaryDao = reviewSummaryDao;
        this.ratingBandDao = ratingBandDao;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeDao.findAll();
    }

    @Override
    public ReviewSummary getReviewSummary(int employeeId)
            throws EmployeeNotFoundException {

        /*
        The procedure itself cannot tell "unknown employee" from
        "employee without reviews" (both give count 0), so the
        employee is checked first.
        */
        Employee employee = requireEmployee(employeeId);

        ReviewSummary summary = reviewSummaryDao.callReviewSummary(employeeId);

        summary.setEmployeeName(employee.getEmployeeName());

        return summary;
    }

    @Override
    public List<ReviewSummary> getAllReviewSummaries() {

        List<ReviewSummary> summaries = new ArrayList<>();

        for (Employee employee : employeeDao.findAll()) {

            ReviewSummary summary =
                    reviewSummaryDao.callReviewSummary(employee.getEmployeeId());

            summary.setEmployeeName(employee.getEmployeeName());

            summaries.add(summary);
        }

        return summaries;
    }

    @Override
    public long addReview(int employeeId,
                          String reviewPeriod,
                          BigDecimal rating,
                          String reviewer,
                          String comments)
            throws EmployeeNotFoundException,
                   InvalidInputException {

        requireEmployee(employeeId);

        String period = reviewPeriod == null ? "" : reviewPeriod.trim().toUpperCase();

        if (!period.matches(PERIOD_PATTERN)) {
            throw new InvalidInputException(
                    "Review period must look like 2026-Q2 (year, dash, Q1 to Q4)."
            );
        }

        validateRating(rating);

        if (reviewer == null || reviewer.trim().isEmpty()) {
            throw new InvalidInputException("Reviewer cannot be blank.");
        }

        if (reviewer.trim().length() > 120) {
            throw new InvalidInputException(
                    "Reviewer cannot be longer than 120 characters."
            );
        }

        String cleanComments =
                comments == null || comments.trim().isEmpty() ? null : comments.trim();

        if (cleanComments != null && cleanComments.length() > 500) {
            throw new InvalidInputException(
                    "Comments cannot be longer than 500 characters."
            );
        }

        return reviewDao.add(new PerformanceReview(
                employeeId, period, rating, reviewer.trim(), cleanComments));
    }

    @Override
    public List<RatingBand> getRatingBands() {
        return ratingBandDao.findAll();
    }

    @Override
    public void updateRatingBand(String bandName, BigDecimal minRating)
            throws InvalidInputException {

        if (minRating == null
                || minRating.signum() < 0
                || minRating.compareTo(MAX_RATING) > 0) {

            throw new InvalidInputException(
                    "Threshold must be between 0.00 and 5.00."
            );
        }

        if (minRating.scale() > 2) {
            throw new InvalidInputException(
                    "Threshold can have at most 2 decimal places."
            );
        }

        String name = bandName == null ? "" : bandName.trim().toUpperCase();

        if (!ratingBandDao.updateMinRating(name, minRating)) {
            throw new InvalidInputException("No rating band named '" + name + "'.");
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Employee requireEmployee(int employeeId)
            throws EmployeeNotFoundException {

        Employee employee = employeeDao.findById(employeeId);

        if (employee == null) {
            throw new EmployeeNotFoundException(
                    "Employee with ID " + employeeId + " was not found."
            );
        }

        return employee;
    }

    private void validateRating(BigDecimal rating)
            throws InvalidInputException {

        if (rating == null
                || rating.compareTo(MIN_RATING) < 0
                || rating.compareTo(MAX_RATING) > 0) {

            throw new InvalidInputException(
                    "Rating must be between 1.00 and 5.00."
            );
        }

        if (rating.scale() > 2) {
            throw new InvalidInputException(
                    "Rating can have at most 2 decimal places."
            );
        }
    }
}
