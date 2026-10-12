package com.company.jdbc.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.RatingBand;

public class RatingBandDaoImpl implements RatingBandDao {

    private static final int MYSQL_DUPLICATE_KEY = 1062;

    @Override
    public List<RatingBand> findAll() {

        List<RatingBand> bands = new ArrayList<>();

        String sql = "SELECT band_name, min_rating "
                   + "FROM rating_bands "
                   + "ORDER BY min_rating DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                bands.add(new RatingBand(
                        resultSet.getString("band_name"),
                        resultSet.getBigDecimal("min_rating")
                ));
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read rating bands", e);
        }

        return bands;
    }

    @Override
    public boolean updateMinRating(String bandName, BigDecimal minRating)
            throws InvalidInputException {

        String sql = "UPDATE rating_bands "
                   + "SET min_rating = ? "
                   + "WHERE band_name = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, minRating);
            statement.setString(2, bandName);

            return statement.executeUpdate() == 1;

        } catch (SQLIntegrityConstraintViolationException e) {

            if (e.getErrorCode() == MYSQL_DUPLICATE_KEY) {
                throw new InvalidInputException(
                        "Another band already starts at " + minRating
                        + ". Each band needs a different threshold."
                );
            }

            throw new DataAccessException("Unable to update rating band", e);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to update rating band", e);
        }
    }
}
