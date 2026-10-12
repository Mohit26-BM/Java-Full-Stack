package com.company.jdbc.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
Everything returned by sp_employee_review_summary:
the 4 OUT parameters plus the result set of reviews.

averageRating and latestPeriod are NULL (null in Java)
when the employee has no reviews.
*/
public class ReviewSummary {

    private final int employeeId;
    private final BigDecimal averageRating;
    private final int reviewCount;
    private final String latestPeriod;
    private final String ratingBand;
    private final List<PerformanceReview> reviews;

    private String employeeName;

    public ReviewSummary(int employeeId,
                         BigDecimal averageRating,
                         int reviewCount,
                         String latestPeriod,
                         String ratingBand,
                         List<PerformanceReview> reviews) {

        this.employeeId = employeeId;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.latestPeriod = latestPeriod;
        this.ratingBand = ratingBand;
        this.reviews = reviews == null ? new ArrayList<PerformanceReview>() : reviews;
    }

    public boolean hasReviews() {
        return reviewCount > 0;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getLatestPeriod() {
        return latestPeriod;
    }

    public String getRatingBand() {
        return ratingBand;
    }

    public List<PerformanceReview> getReviews() {
        return reviews;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}
