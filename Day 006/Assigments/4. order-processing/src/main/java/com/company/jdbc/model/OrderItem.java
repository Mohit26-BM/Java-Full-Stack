package com.company.jdbc.model;

import java.math.BigDecimal;

/*
One row of order_items.

unitPrice is the price AT THE TIME OF ORDER (copied from products),
so later price changes do not alter old orders.
productName is filled only when reading (JOIN with products).
*/
public class OrderItem {

    private long orderItemId;
    private long orderId;
    private long productId;
    private int quantity;
    private BigDecimal unitPrice;

    private String productName;

    public OrderItem() {
    }

    public OrderItem(long orderId, long productId, int quantity, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /*
    quantity x unit price
    */
    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {

        return String.format(
                "%-6d %-6d %-30.30s %5d x %12s = %14s",
                orderItemId,
                productId,
                productName,
                quantity,
                "Rs. " + unitPrice,
                "Rs. " + getLineTotal()
        );
    }
}
