package com.company.jdbc.model;

/*
One line of a bulk restock: add `quantity` copies to book `bookId`.
*/
public class RestockRequest {

    private final int bookId;
    private final int quantity;

    public RestockRequest(int bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public int getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }
}
