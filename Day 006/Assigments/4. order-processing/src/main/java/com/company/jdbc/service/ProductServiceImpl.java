package com.company.jdbc.service;

import java.math.BigDecimal;
import java.util.List;

import com.company.jdbc.dao.ProductDao;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.exception.ProductNotFoundException;
import com.company.jdbc.model.Product;

public class ProductServiceImpl implements ProductService {

    /*
    Largest value that fits in DECIMAL(12,2).
    */
    private static final BigDecimal MAX_PRICE = new BigDecimal("9999999999.99");

    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public long addProduct(String productName, BigDecimal unitPrice, int stockQty)
            throws InvalidInputException {

        if (productName == null || productName.trim().isEmpty()) {
            throw new InvalidInputException("Product name cannot be blank.");
        }

        String name = productName.trim();

        if (name.length() > 150) {
            throw new InvalidInputException(
                    "Product name cannot be longer than 150 characters."
            );
        }

        validatePrice(unitPrice);

        if (stockQty < 0) {
            throw new InvalidInputException("Stock cannot be negative.");
        }

        return productDao.add(new Product(name, unitPrice, stockQty));
    }

    @Override
    public Product getProduct(long productId)
            throws ProductNotFoundException {

        Product product = productDao.findById(productId);

        if (product == null) {
            throw new ProductNotFoundException(
                    "Product with ID " + productId + " was not found."
            );
        }

        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    @Override
    public void updatePrice(long productId, BigDecimal newPrice)
            throws InvalidInputException,
                   ProductNotFoundException {

        validatePrice(newPrice);

        if (!productDao.updatePrice(productId, newPrice)) {
            throw new ProductNotFoundException(
                    "Product with ID " + productId + " was not found."
            );
        }
    }

    @Override
    public void addStock(long productId, int quantity)
            throws InvalidInputException,
                   ProductNotFoundException {

        if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be greater than 0.");
        }

        if (!productDao.addStock(productId, quantity)) {
            throw new ProductNotFoundException(
                    "Product with ID " + productId + " was not found."
            );
        }
    }

    private void validatePrice(BigDecimal price)
            throws InvalidInputException {

        if (price == null || price.signum() <= 0) {
            throw new InvalidInputException("Price must be greater than 0.");
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
}
