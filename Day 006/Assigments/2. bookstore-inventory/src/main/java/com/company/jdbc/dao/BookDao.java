package com.company.jdbc.dao;

import java.math.BigDecimal;
import java.util.List;

import com.company.jdbc.exception.DuplicateBookException;
import com.company.jdbc.model.Book;
import com.company.jdbc.model.RestockRequest;

public interface BookDao {

    /*
    Returns the generated book_id.
    */
    int add(Book book)
            throws DuplicateBookException;

    /*
    Returns null when no book has this ID.
    */
    Book findById(int bookId);

    List<Book> findAll();

    /*
    Updates ISBN, title, author, category and price.
    Stock is changed ONLY through increaseStock / decreaseStock.
    */
    boolean update(Book book)
            throws DuplicateBookException;

    boolean delete(int bookId);

    /*
    Partial, case-insensitive title match using LIKE.
    */
    List<Book> searchByTitle(String keyword);

    /*
    Any argument may be null, meaning "no filter".
    */
    List<Book> search(Integer categoryId,
                      BigDecimal minPrice,
                      BigDecimal maxPrice);

    /*
    Returns false when the book does not exist.
    */
    boolean increaseStock(int bookId, int quantity);

    /*
    Returns false when the book does not exist OR when
    there is not enough stock. Stock never goes negative.
    */
    boolean decreaseStock(int bookId, int quantity);

    /*
    Books with stock_qty strictly below the threshold.
    */
    List<Book> findLowStock(int threshold);

    /*
    Optional challenge: bulk restock with addBatch() / executeBatch().
    Returns the update count for each request, in the same order.
    */
    int[] restockBatch(List<RestockRequest> requests);
}
