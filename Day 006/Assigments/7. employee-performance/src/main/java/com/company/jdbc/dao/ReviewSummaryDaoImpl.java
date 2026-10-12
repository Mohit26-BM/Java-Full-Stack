package com.company.jdbc.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.model.PerformanceReview;
import com.company.jdbc.model.ReviewSummary;

public class ReviewSummaryDaoImpl implements ReviewSummaryDao {

    /*
    JDBC escape syntax for calling a stored procedure.
    One ? per parameter: 1 IN + 4 OUT.
    */
    private static final String CALL_SUMMARY =
            "{call sp_employee_review_summary(?, ?, ?, ?, ?)}";

    // Parameter positions, so the code below reads clearly
    private static final int IN_EMPLOYEE_ID     = 1;
    private static final int OUT_AVERAGE_RATING = 2;
    private static final int OUT_REVIEW_COUNT   = 3;
    private static final int OUT_LATEST_PERIOD  = 4;
    private static final int OUT_RATING_BAND    = 5;

    @Override
    public ReviewSummary callReviewSummary(int employeeId) {

        /*
        try-with-resources closes the CallableStatement and the
        Connection automatically, even when an exception occurs.
        */
        try (Connection connection = DBConnection.getConnection();
             CallableStatement call = connection.prepareCall(CALL_SUMMARY)) {

            // ---------- 1. IN parameter ----------

            call.setInt(IN_EMPLOYEE_ID, employeeId);

            // ---------- 2. register every OUT parameter with its SQL type ----------

            call.registerOutParameter(OUT_AVERAGE_RATING, Types.DECIMAL);
            call.registerOutParameter(OUT_REVIEW_COUNT, Types.INTEGER);
            call.registerOutParameter(OUT_LATEST_PERIOD, Types.VARCHAR);
            call.registerOutParameter(OUT_RATING_BAND, Types.VARCHAR);

            // ---------- 3. execute ----------

            /*
            execute() is used (not executeQuery) because the procedure
            can return result sets AND OUT parameters.
            It returns true when the first result is a ResultSet.
            */
            boolean isResultSet = call.execute();

            // ---------- 4. read the result set(s) FIRST ----------

            List<PerformanceReview> reviews = readReviews(call, isResultSet, employeeId);

            // ---------- 5. then read the OUT parameters ----------

            BigDecimal averageRating = call.getBigDecimal(OUT_AVERAGE_RATING);
            if (call.wasNull()) {
                averageRating = null;            // no reviews -> SQL NULL
            }

            int reviewCount = call.getInt(OUT_REVIEW_COUNT);

            String latestPeriod = call.getString(OUT_LATEST_PERIOD);   // null if no reviews

            String ratingBand = call.getString(OUT_RATING_BAND);

            return new ReviewSummary(
                    employeeId,
                    averageRating,
                    reviewCount,
                    latestPeriod,
                    ratingBand,
                    reviews
            );

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to call sp_employee_review_summary for employee "
                    + employeeId + ". Did you run procedure.sql?", e
            );
        }
    }

    /*
    Walks through every result the procedure produced.
    The first ResultSet holds the review rows; any further
    results are skipped. Each ResultSet is closed after use.
    */
    private List<PerformanceReview> readReviews(CallableStatement call,
                                                boolean isResultSet,
                                                int employeeId)
            throws SQLException {

        List<PerformanceReview> reviews = new ArrayList<>();
        boolean firstResultSet = true;

        while (true) {

            if (isResultSet) {

                try (ResultSet resultSet = call.getResultSet()) {

                    while (firstResultSet && resultSet.next()) {

                        PerformanceReview review = new PerformanceReview(
                                employeeId,
                                resultSet.getString("review_period"),
                                resultSet.getBigDecimal("rating"),
                                resultSet.getString("reviewer"),
                                resultSet.getString("comments")
                        );

                        review.setReviewId(resultSet.getLong("review_id"));

                        reviews.add(review);
                    }
                }

                firstResultSet = false;

            } else if (call.getUpdateCount() == -1) {

                // No more results of any kind
                break;
            }

            isResultSet = call.getMoreResults();
        }

        return reviews;
    }
}
