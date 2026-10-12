package com.company.jdbc.service;

import java.math.BigDecimal;
import java.util.List;

import com.company.jdbc.config.AppConfig;
import com.company.jdbc.dao.AuthorDao;
import com.company.jdbc.dao.BookDao;
import com.company.jdbc.dao.CategoryDao;
import com.company.jdbc.exception.AuthorNotFoundException;
import com.company.jdbc.exception.BookNotFoundException;
import com.company.jdbc.exception.CategoryNotFoundException;
import com.company.jdbc.exception.DuplicateBookException;
import com.company.jdbc.exception.InsufficientStockException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Book;
import com.company.jdbc.model.RestockRequest;

public class BookServiceImpl implements BookService {

    /*
    ISBN-10 (last character may be X) or ISBN-13,
    after removing hyphens and spaces.
    */
    private static final String ISBN_PATTERN = "\\d{9}[\\dX]|\\d{13}";

    private static final int MAX_TITLE_LENGTH = 200;

    /*
    Largest value that fits in DECIMAL(10,2).
    */
    private static final BigDecimal MAX_PRICE = new BigDecimal("99999999.99");

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final CategoryDao categoryDao;

    public BookServiceImpl(BookDao bookDao,
                           AuthorDao authorDao,
                           CategoryDao categoryDao) {

        this.bookDao = bookDao;
        this.authorDao = authorDao;
        this.categoryDao = categoryDao;
    }

    @Override
    public int addBook(Book book)
            throws InvalidInputException,
                   AuthorNotFoundException,
                   CategoryNotFoundException,
                   DuplicateBookException {

        validateBookDetails(book);

        if (book.getStockQty() < 0) {
            throw new InvalidInputException(
                    "Opening stock cannot be negative."
            );
        }

        int bookId = bookDao.add(book);

        book.setBookId(bookId);

        return bookId;
    }

    @Override
    public Book getBookById(int bookId)
            throws BookNotFoundException {

        Book book = bookDao.findById(bookId);

        if (book == null) {
            throw new BookNotFoundException(
                    "Book with ID " + bookId + " was not found."
            );
        }

        return book;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookDao.findAll();
    }

    @Override
    public void updateBook(Book book)
            throws InvalidInputException,
                   BookNotFoundException,
                   AuthorNotFoundException,
                   CategoryNotFoundException,
                   DuplicateBookException {

        validateBookDetails(book);

        if (!bookDao.update(book)) {
            throw new BookNotFoundException(
                    "Book with ID " + book.getBookId() + " was not found."
            );
        }
    }

    @Override
    public void deleteBook(int bookId)
            throws BookNotFoundException {

        if (!bookDao.delete(bookId)) {
            throw new BookNotFoundException(
                    "Book with ID " + bookId + " was not found."
            );
        }
    }

    @Override
    public List<Book> searchByTitle(String keyword)
            throws InvalidInputException {

        String cleaned = InputValidator.requireText(
                keyword, "Search text", MAX_TITLE_LENGTH);

        return bookDao.searchByTitle(cleaned);
    }

    @Override
    public List<Book> search(Integer categoryId,
                             BigDecimal minPrice,
                             BigDecimal maxPrice)
            throws InvalidInputException,
                   CategoryNotFoundException {

        if (categoryId != null) {
            requireCategory(categoryId);
        }

        if (minPrice != null && minPrice.signum() < 0) {
            throw new InvalidInputException(
                    "Minimum price cannot be negative."
            );
        }

        if (maxPrice != null && maxPrice.signum() < 0) {
            throw new InvalidInputException(
                    "Maximum price cannot be negative."
            );
        }

        if (minPrice != null
                && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {

            throw new InvalidInputException(
                    "Minimum price cannot be greater than maximum price."
            );
        }

        return bookDao.search(categoryId, minPrice, maxPrice);
    }

    @Override
    public int increaseStock(int bookId, int quantity)
            throws InvalidInputException,
                   BookNotFoundException {

        InputValidator.requirePositive(quantity, "Quantity");

        if (!bookDao.increaseStock(bookId, quantity)) {
            throw new BookNotFoundException(
                    "Book with ID " + bookId + " was not found."
            );
        }

        return getBookById(bookId).getStockQty();
    }

    @Override
    public int decreaseStock(int bookId, int quantity)
            throws InvalidInputException,
                   BookNotFoundException,
                   InsufficientStockException {

        InputValidator.requirePositive(quantity, "Quantity");

        if (bookDao.decreaseStock(bookId, quantity)) {
            return getBookById(bookId).getStockQty();
        }

        /*
        The UPDATE changed nothing. Find out why:
        the book does not exist, or there is not enough stock.
        */
        Book book = getBookById(bookId);

        throw new InsufficientStockException(
                "Cannot remove " + quantity + " copies of '"
                + book.getTitle() + "'. Only "
                + book.getStockQty() + " in stock. Stock was not changed."
        );
    }

    @Override
    public List<Book> getLowStockReport(int threshold)
            throws InvalidInputException {

        InputValidator.requirePositive(threshold, "Threshold");

        return bookDao.findLowStock(threshold);
    }

    @Override
    public int getDefaultLowStockThreshold() {
        return AppConfig.getLowStockThreshold();
    }

    @Override
    public int[] bulkRestock(List<RestockRequest> requests)
            throws InvalidInputException,
                   BookNotFoundException {

        if (requests == null || requests.isEmpty()) {
            throw new InvalidInputException(
                    "Add at least one book to restock."
            );
        }

        /*
        Validate every line BEFORE sending the batch,
        so the batch either fully succeeds or is not sent.
        */
        for (RestockRequest request : requests) {

            InputValidator.requirePositive(
                    request.getQuantity(),
                    "Quantity for book " + request.getBookId());

            getBookById(request.getBookId());
        }

        return bookDao.restockBatch(requests);
    }

    // =========================================================
    // VALIDATION HELPERS
    // =========================================================

    /*
    Validates and cleans ISBN, title, author, category and price.
    Runs BEFORE any INSERT or UPDATE.
    */
    private void validateBookDetails(Book book)
            throws InvalidInputException,
                   AuthorNotFoundException,
                   CategoryNotFoundException {

        if (book == null) {
            throw new InvalidInputException(
                    "Book details cannot be empty."
            );
        }

        book.setIsbn(validateIsbn(book.getIsbn()));

        book.setTitle(InputValidator.requireText(
                book.getTitle(), "Title", MAX_TITLE_LENGTH));

        validatePrice(book.getPrice());

        if (authorDao.findById(book.getAuthorId()) == null) {
            throw new AuthorNotFoundException(
                    "Author with ID " + book.getAuthorId()
                    + " does not exist."
            );
        }

        requireCategory(book.getCategoryId());
    }

    private String validateIsbn(String isbn)
            throws InvalidInputException {

        if (isbn == null || isbn.trim().isEmpty()) {
            throw new InvalidInputException("ISBN cannot be blank.");
        }

        /*
        978-0-13-468599-1  ->  9780134685991
        */
        String cleaned = isbn.replace("-", "")
                             .replace(" ", "")
                             .toUpperCase();

        if (!cleaned.matches(ISBN_PATTERN)) {
            throw new InvalidInputException(
                    "ISBN must be 10 or 13 digits "
                    + "(ISBN-10 may end with X)."
            );
        }

        return cleaned;
    }

    private void validatePrice(BigDecimal price)
            throws InvalidInputException {

        if (price == null) {
            throw new InvalidInputException("Price is required.");
        }

        if (price.signum() <= 0) {
            throw new InvalidInputException(
                    "Price must be greater than 0."
            );
        }

        if (price.scale() > 2) {
            throw new InvalidInputException(
                    "Price can have at most 2 decimal places."
            );
        }

        if (price.compareTo(MAX_PRICE) > 0) {
            throw new InvalidInputException("Price is too large.");
        }
    }

    private void requireCategory(int categoryId)
            throws CategoryNotFoundException {

        if (categoryDao.findById(categoryId) == null) {
            throw new CategoryNotFoundException(
                    "Category with ID " + categoryId + " does not exist."
            );
        }
    }
}
