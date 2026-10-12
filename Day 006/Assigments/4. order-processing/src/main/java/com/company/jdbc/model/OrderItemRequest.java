package com.company.jdbc.model;

/*
What the customer asks for: "quantity units of productId".
The price is NOT part of the request; it is read from the
products table inside the order transaction.
*/
public class OrderItemRequest {

    private final long productId;
    private final int quantity;

    public OrderItemRequest(long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
