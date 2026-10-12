package com.company.jdbc.service;

import java.math.BigDecimal;
import java.util.List;

import com.company.jdbc.exception.AuthorNotFoundException;
import com.company.jdbc.exception.BookNotFoundException;
import com.company.jdbc.exception.CategoryNotFoundException;
import com.company.jdbc.exception.DuplicateBookException;
import com.company.jdbc.exception.InsufficientStockException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Book;
import com.company.jdbc.model.RestockRequest;

public interface BookService {

    int addBook(Book book)
            throws InvalidInputException,
                   AuthorNotFoundException,
                   CategoryNotFoundException,
                   DuplicateBookException;

    Book getBookById(int bookId)
            throws BookNotFoundException;

    List<Book> getAllBooks();

    /*
    Updates ISBN, title, author, category and price (not stock).
    */
    void updateBook(Book book)
            throws InvalidInputException,
                   BookNotFoundException,
                   AuthorNotFoundException,
                   CategoryNotFoundException,
                   DuplicateBookException;

    void deleteBook(int bookId)
            throws BookNotFoundException;

    List<Book> searchByTitle(String keyword)
            throws InvalidInputException;

    /*
    categoryId, minPrice and maxPrice may each be null (= any).
    */
    List<Book> search(Integer categoryId,
                      BigDecimal minPrice,
                      BigDecimal maxPrice)
            throws InvalidInputException,
                   CategoryNotFoundException;

    /*
    Returns the new stock quantity.
    */
    int increaseStock(int bookId, int quantity)
            throws InvalidInputException,
                   BookNotFoundException;

    /*
    Returns the new stock quantity.
    */
    int decreaseStock(int bookId, int quantity)
            throws InvalidInputException,
                   BookNotFoundException,
                   InsufficientStockException;

    List<Book> getLowStockReport(int threshold)
            throws InvalidInputException;

    int getDefaultLowStockThreshold();

    int[] bulkRestock(List<RestockRequest> requests)
            throws InvalidInputException,
                   BookNotFoundException;
}
