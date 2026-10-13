package com.company.boot.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * @Entity - this class is mapped to a database table; each object = one row.
 * @Table  - the table name (otherwise it would be "product").
 *
 * Hibernate reads/writes the FIELDS below and generates all INSERT / SELECT /
 * UPDATE / DELETE statements itself.
 */
@Entity
@Table(name = "a14_products")
public class Product {

    /**
     * @Id             - the primary key; identifies the entity.
     * @GeneratedValue - IDENTITY = the database generates it (MySQL AUTO_INCREMENT).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** JPA requires a no-argument constructor (Hibernate uses it to build objects from rows). */
    protected Product() {
    }

    public Product(String name, String category, BigDecimal price, Integer stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    /** JPA lifecycle callback: runs just before the INSERT. */
    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now().withNano(0);
        updatedAt = createdAt;
    }

    /** JPA lifecycle callback: runs just before an UPDATE. */
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now().withNano(0);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
