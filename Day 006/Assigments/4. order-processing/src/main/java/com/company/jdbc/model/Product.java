package com.company.jdbc.model;

import java.math.BigDecimal;

public class Product {

    private long productId;
    private String productName;
    private BigDecimal unitPrice;
    private int stockQty;

    public Product() {
    }

    public Product(String productName, BigDecimal unitPrice, int stockQty) {
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.stockQty = stockQty;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    @Override
    public String toString() {

        return String.format(
                "%-4d %-30.30s %14s %7d",
                productId,
                productName,
                "Rs. " + unitPrice,
                stockQty
        );
    }
}
