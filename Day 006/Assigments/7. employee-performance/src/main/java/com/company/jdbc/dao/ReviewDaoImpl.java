package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.model.PerformanceReview;

public class ReviewDaoImpl implements ReviewDao {

    @Override
    public long add(PerformanceReview review) {

        String sql = "INSERT INTO performance_reviews "
                   + "(employee_id, review_period, rating, reviewer, comments) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, review.getEmployeeId());
            statement.setString(2, review.getReviewPeriod());
            statement.setBigDecimal(3, review.getRating());
            statement.setString(4, review.getReviewer());

            if (review.getComments() == null) {
                statement.setNull(5, Types.VARCHAR);
            } else {
                statement.setString(5, review.getComments());
            }

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to add review", e);
        }
    }
}
