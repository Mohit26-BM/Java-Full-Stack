package com.company.jdbc.dao;

import java.math.BigDecimal;
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
import com.company.jdbc.exception.DuplicateBookException;
import com.company.jdbc.model.Book;
import com.company.jdbc.model.RestockRequest;

public class BookDaoImpl implements BookDao {

    /*
    Every read uses JOINs so results show author and
    category NAMES instead of only IDs.
    */
    private static final String SELECT_BOOK =
            "SELECT b.book_id, b.isbn, b.title, "
          + "b.author_id, a.author_name, "
          + "b.category_id, c.category_name, "
          + "b.price, b.stock_qty "
          + "FROM books b "
          + "JOIN authors a ON b.author_id = a.author_id "
          + "JOIN categories c ON b.category_id = c.category_id ";

    @Override
    public int add(Book book)
            throws DuplicateBookException {

        String sql = "INSERT INTO books "
                   + "(isbn, title, author_id, category_id, price, stock_qty) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());
            statement.setInt(3, book.getAuthorId());
            statement.setInt(4, book.getCategoryId());
            statement.setBigDecimal(5, book.getPrice());
            statement.setInt(6, book.getStockQty());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }

        } catch (SQLIntegrityConstraintViolationException e) {

            throw toDuplicateException(book, e);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to add book", e);
        }
    }

    @Override
    public Book findById(int bookId) {

        String sql = SELECT_BOOK + "WHERE b.book_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to read book " + bookId, e
            );
        }
    }

    @Override
    public List<Book> findAll() {

        String sql = SELECT_BOOK + "ORDER BY b.title";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            return readList(statement);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read books", e);
        }
    }

    @Override
    public boolean update(Book book)
            throws DuplicateBookException {

        String sql = "UPDATE books "
                   + "SET isbn = ?, title = ?, author_id = ?, "
                   + "category_id = ?, price = ? "
                   + "WHERE book_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());
            statement.setInt(3, book.getAuthorId());
            statement.setInt(4, book.getCategoryId());
            statement.setBigDecimal(5, book.getPrice());
            statement.setInt(6, book.getBookId());

            return statement.executeUpdate() == 1;

        } catch (SQLIntegrityConstraintViolationException e) {

            throw toDuplicateException(book, e);

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to update book " + book.getBookId(), e
            );
        }
    }

    @Override
    public boolean delete(int bookId) {

        String sql = "DELETE FROM books WHERE book_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookId);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to delete book " + bookId, e
            );
        }
    }

    @Override
    public List<Book> searchByTitle(String keyword) {

        /*
        The keyword is passed as a PARAMETER, never concatenated
        into the SQL, so it is safe from SQL injection.

        % and _ typed by the user are escaped so they are treated
        as normal characters, not as LIKE wildcards.
        */
        String sql = SELECT_BOOK
                   + "WHERE b.title LIKE ? "
                   + "ORDER BY b.title";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, "%" + escapeLike(keyword) + "%");

            return readList(statement);

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to search titles", e
            );
        }
    }

    @Override
    public List<Book> search(Integer categoryId,
                             BigDecimal minPrice,
                             BigDecimal maxPrice) {

        /*
        The WHERE clause is built only from fixed SQL pieces.
        User values are always bound as parameters.
        */
        StringBuilder sql = new StringBuilder(SELECT_BOOK);
        sql.append("WHERE 1 = 1 ");

        List<Object> parameters = new ArrayList<>();

        if (categoryId != null) {
            sql.append("AND b.category_id = ? ");
            parameters.add(categoryId);
        }

        if (minPrice != null) {
            sql.append("AND b.price >= ? ");
            parameters.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append("AND b.price <= ? ");
            parameters.add(maxPrice);
        }

        sql.append("ORDER BY b.price, b.title");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            return readList(statement);

        } catch (SQLException e) {

            throw new DataAccessException("Unable to search books", e);
        }
    }

    @Override
    public boolean increaseStock(int bookId, int quantity) {

        /*
        stock_qty = stock_qty + ? is done by MySQL in one step,
        so two users restocking at once cannot overwrite each other.
        */
        String sql = "UPDATE books "
                   + "SET stock_qty = stock_qty + ? "
                   + "WHERE book_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setInt(2, bookId);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to increase stock for book " + bookId, e
            );
        }
    }

    @Override
    public boolean decreaseStock(int bookId, int quantity) {

        /*
        "AND stock_qty >= ?" means the row is only updated when
        there is enough stock. The check and the update happen
        in ONE statement, so stock can never become negative.
        */
        String sql = "UPDATE books "
                   + "SET stock_qty = stock_qty - ? "
                   + "WHERE book_id = ? "
                   + "AND stock_qty >= ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setInt(2, bookId);
            statement.setInt(3, quantity);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to decrease stock for book " + bookId, e
            );
        }
    }

    @Override
    public List<Book> findLowStock(int threshold) {

        String sql = SELECT_BOOK
                   + "WHERE b.stock_qty < ? "
                   + "ORDER BY b.stock_qty, b.title";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, threshold);

            return readList(statement);

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to build low-stock report", e
            );
        }
    }

    @Override
    public int[] restockBatch(List<RestockRequest> requests) {

        String sql = "UPDATE books "
                   + "SET stock_qty = stock_qty + ? "
                   + "WHERE book_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            /*
            All updates are sent together and committed together.
            If anything fails, rollback() undoes the whole batch.
            */
            connection.setAutoCommit(false);

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                for (RestockRequest request : requests) {

                    statement.setInt(1, request.getQuantity());
                    statement.setInt(2, request.getBookId());

                    statement.addBatch();
                }

                int[] updateCounts = statement.executeBatch();

                connection.commit();

                return updateCounts;

            } catch (SQLException e) {

                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Bulk restock failed. No stock was changed.", e
            );
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private List<Book> readList(PreparedStatement statement)
            throws SQLException {

        List<Book> books = new ArrayList<>();

        try (ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                books.add(mapRow(resultSet));
            }
        }

        return books;
    }

    private Book mapRow(ResultSet resultSet) throws SQLException {

        Book book = new Book();

        book.setBookId(resultSet.getInt("book_id"));
        book.setIsbn(resultSet.getString("isbn"));
        book.setTitle(resultSet.getString("title"));
        book.setAuthorId(resultSet.getInt("author_id"));
        book.setAuthorName(resultSet.getString("author_name"));
        book.setCategoryId(resultSet.getInt("category_id"));
        book.setCategoryName(resultSet.getString("category_name"));
        book.setPrice(resultSet.getBigDecimal("price"));
        book.setStockQty(resultSet.getInt("stock_qty"));

        return book;
    }

    /*
    MySQL uses \ as the default LIKE escape character.
    */
    private String escapeLike(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private DuplicateBookException toDuplicateException(
            Book book,
            SQLIntegrityConstraintViolationException e) {

        if (e.getErrorCode() == MySqlErrorCodes.DUPLICATE_KEY) {
            return new DuplicateBookException(
                    "A book with ISBN " + book.getIsbn() + " already exists."
            );
        }

        /*
        Foreign key errors should not happen because the service
        checks the author and category first.
        */
        throw new DataAccessException(
                "Database constraint violated: " + e.getMessage(), e
        );
    }
}
