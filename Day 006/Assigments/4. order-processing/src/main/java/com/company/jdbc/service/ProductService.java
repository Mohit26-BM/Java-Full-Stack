package com.company.jdbc.service;

import java.math.BigDecimal;
import java.util.List;

import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.exception.ProductNotFoundException;
import com.company.jdbc.model.Product;

public interface ProductService {

    long addProduct(String productName, BigDecimal unitPrice, int stockQty)
            throws InvalidInputException;

    Product getProduct(long productId)
            throws ProductNotFoundException;

    List<Product> getAllProducts();

    void updatePrice(long productId, BigDecimal newPrice)
            throws InvalidInputException,
                   ProductNotFoundException;

    void addStock(long productId, int quantity)
            throws InvalidInputException,
                   ProductNotFoundException;
}
