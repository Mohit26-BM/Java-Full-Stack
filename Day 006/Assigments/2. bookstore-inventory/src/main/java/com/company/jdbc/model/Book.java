package com.company.jdbc.model;

import java.math.BigDecimal;

/*
Plain model class. Validation is done in BookServiceImpl.

authorName and categoryName are filled only when reading
(via JOIN) so reports show names instead of IDs.
*/
public class Book {

    private int bookId;
    private String isbn;
    private String title;
    private int authorId;
    private int categoryId;
    private BigDecimal price;
    private int stockQty;

    private String authorName;
    private String categoryName;

    public Book() {
    }

    public Book(String isbn,
                String title,
                int authorId,
                int categoryId,
                BigDecimal price,
                int stockQty) {

        this.isbn = isbn;
        this.title = title;
        this.authorId = authorId;
        this.categoryId = categoryId;
        this.price = price;
        this.stockQty = stockQty;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /*
    One table row. %-32.32s pads AND cuts long titles to 32 characters.
    */
    @Override
    public String toString() {

        return String.format(
                "%-4d %-14s %-32.32s %-18.18s %-12.12s %10s %6d",
                bookId,
                isbn,
                title,
                authorName,
                categoryName,
                "Rs." + price,
                stockQty
        );
    }
}
