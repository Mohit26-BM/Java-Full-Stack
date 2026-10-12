package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.EntityInUseException;
import com.company.jdbc.model.Author;

public class AuthorDaoImpl implements AuthorDao {

    @Override
    public int add(Author author) {

        String sql = "INSERT INTO authors (author_name) VALUES (?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, author.getAuthorName());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to add author", e);
        }
    }

    @Override
    public Author findById(int authorId) {

        String sql = "SELECT author_id, author_name "
                   + "FROM authors "
                   + "WHERE author_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, authorId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to read author " + authorId, e
            );
        }
    }

    @Override
    public List<Author> findAll() {

        List<Author> authors = new ArrayList<>();

        String sql = "SELECT author_id, author_name "
                   + "FROM authors "
                   + "ORDER BY author_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                authors.add(mapRow(resultSet));
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read authors", e);
        }

        return authors;
    }

    @Override
    public boolean update(Author author) {

        String sql = "UPDATE authors "
                   + "SET author_name = ? "
                   + "WHERE author_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, author.getAuthorName());
            statement.setInt(2, author.getAuthorId());

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to update author " + author.getAuthorId(), e
            );
        }
    }

    @Override
    public boolean delete(int authorId)
            throws EntityInUseException {

        String sql = "DELETE FROM authors WHERE author_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, authorId);

            return statement.executeUpdate() == 1;

        } catch (SQLIntegrityConstraintViolationException e) {

            if (e.getErrorCode() == MySqlErrorCodes.ROW_IS_REFERENCED) {
                throw new EntityInUseException(
                        "Author " + authorId + " still has books. "
                        + "Delete or reassign those books first."
                );
            }

            throw new DataAccessException(
                    "Unable to delete author " + authorId, e
            );

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to delete author " + authorId, e
            );
        }
    }

    private Author mapRow(ResultSet resultSet) throws SQLException {

        return new Author(
                resultSet.getInt("author_id"),
                resultSet.getString("author_name")
        );
    }
}
