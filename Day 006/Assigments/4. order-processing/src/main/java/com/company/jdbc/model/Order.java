package com.company.jdbc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
One row of orders, plus (when loaded with details) the customer
name/email and the list of items, all read using JOINs.
*/
public class Order {

    private long orderId;
    private long customerId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;

    private String customerName;
    private String customerEmail;
    private List<OrderItem> items = new ArrayList<>();

    /*
    Sum of (quantity x unit price) over all items.
    Used to verify that the saved total_amount is correct.
    */
    public BigDecimal calculateItemsTotal() {

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : items) {
            total = total.add(item.getLineTotal());
        }

        return total;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    /*
    One summary row (without items).
    */
    @Override
    public String toString() {

        return String.format(
                "%-6d %-20.20s %-19s %-10s %14s",
                orderId,
                customerName,
                orderDate == null ? "-" : orderDate.toString().replace('T', ' '),
                status,
                "Rs. " + totalAmount
        );
    }
}
