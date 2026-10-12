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
import com.company.jdbc.exception.DuplicateCategoryException;
import com.company.jdbc.exception.EntityInUseException;
import com.company.jdbc.model.Category;

public class CategoryDaoImpl implements CategoryDao {

    @Override
    public int add(Category category)
            throws DuplicateCategoryException {

        String sql = "INSERT INTO categories (category_name) VALUES (?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, category.getCategoryName());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }

        } catch (SQLIntegrityConstraintViolationException e) {

            throw toDuplicateException(category, e);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to add category", e);
        }
    }

    @Override
    public Category findById(int categoryId) {

        String sql = "SELECT category_id, category_name "
                   + "FROM categories "
                   + "WHERE category_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, categoryId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to read category " + categoryId, e
            );
        }
    }

    @Override
    public List<Category> findAll() {

        List<Category> categories = new ArrayList<>();

        String sql = "SELECT category_id, category_name "
                   + "FROM categories "
                   + "ORDER BY category_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(mapRow(resultSet));
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read categories", e);
        }

        return categories;
    }

    @Override
    public boolean update(Category category)
            throws DuplicateCategoryException {

        String sql = "UPDATE categories "
                   + "SET category_name = ? "
                   + "WHERE category_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, category.getCategoryName());
            statement.setInt(2, category.getCategoryId());

            return statement.executeUpdate() == 1;

        } catch (SQLIntegrityConstraintViolationException e) {

            throw toDuplicateException(category, e);

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to update category " + category.getCategoryId(), e
            );
        }
    }

    @Override
    public boolean delete(int categoryId)
            throws EntityInUseException {

        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, categoryId);

            return statement.executeUpdate() == 1;

        } catch (SQLIntegrityConstraintViolationException e) {

            if (e.getErrorCode() == MySqlErrorCodes.ROW_IS_REFERENCED) {
                throw new EntityInUseException(
                        "Category " + categoryId + " still has books. "
                        + "Delete or move those books first."
                );
            }

            throw new DataAccessException(
                    "Unable to delete category " + categoryId, e
            );

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to delete category " + categoryId, e
            );
        }
    }

    private DuplicateCategoryException toDuplicateException(
            Category category,
            SQLIntegrityConstraintViolationException e) {

        if (e.getErrorCode() == MySqlErrorCodes.DUPLICATE_KEY) {
            return new DuplicateCategoryException(
                    "Category '" + category.getCategoryName()
                    + "' already exists."
            );
        }

        throw new DataAccessException(
                "Database constraint violated: " + e.getMessage(), e
        );
    }

    private Category mapRow(ResultSet resultSet) throws SQLException {

        return new Category(
                resultSet.getInt("category_id"),
                resultSet.getString("category_name")
        );
    }
}
