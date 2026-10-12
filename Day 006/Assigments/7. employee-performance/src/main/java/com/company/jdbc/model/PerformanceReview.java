package com.company.jdbc.model;

import java.math.BigDecimal;

public class PerformanceReview {

    private long reviewId;
    private int employeeId;
    private String reviewPeriod;
    private BigDecimal rating;
    private String reviewer;
    private String comments;

    public PerformanceReview() {
    }

    public PerformanceReview(int employeeId,
                             String reviewPeriod,
                             BigDecimal rating,
                             String reviewer,
                             String comments) {

        this.employeeId = employeeId;
        this.reviewPeriod = reviewPeriod;
        this.rating = rating;
        this.reviewer = reviewer;
        this.comments = comments;
    }

    public long getReviewId() {
        return reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getReviewPeriod() {
        return reviewPeriod;
    }

    public void setReviewPeriod(String reviewPeriod) {
        this.reviewPeriod = reviewPeriod;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {

        return String.format(
                "%-6d %-9s %6s  %-16.16s %s",
                reviewId,
                reviewPeriod,
                rating,
                reviewer,
                comments == null ? "-" : comments
        );
    }
}
