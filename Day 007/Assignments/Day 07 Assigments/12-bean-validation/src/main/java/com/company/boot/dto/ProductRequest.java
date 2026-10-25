package com.company.boot.dto;

import java.math.BigDecimal;
import java.util.List;

import com.company.boot.validation.DiscountLessThanPrice;
import com.company.boot.validation.ValidSku;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for POST /api/products.
 *
 * Every rule is DECLARED with an annotation - there is no if/else checking code.
 * Spring runs them all when the controller parameter is marked @Valid.
 *
 * Note: apart from @NotNull / @NotBlank, constraints treat null as VALID
 * (e.g. discountPrice may be omitted, but if present it must be >= 0).
 */
@DiscountLessThanPrice          // custom CLASS-level rule: compares two fields
public class ProductRequest {

    @NotBlank(message = "name is required")
    @Size(min = 3, max = 100, message = "name must be between {min} and {max} characters")
    private String name;

    @NotBlank(message = "sku is required")
    @ValidSku                   // custom FIELD-level rule
    private String sku;

    @NotBlank(message = "category is required")
    private String category;

    @NotNull(message = "price is required")
    @Positive(message = "price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "price may have at most {fraction} decimal places")
    private BigDecimal price;

    @PositiveOrZero(message = "discountPrice cannot be negative")
    private BigDecimal discountPrice;

    @NotNull(message = "stock is required")
    @PositiveOrZero(message = "stock cannot be negative")
    private Integer stock;

    @NotBlank(message = "supplierEmail is required")
    @Email(message = "supplierEmail must be a valid e-mail address")
    private String supplierEmail;

    /** Container-element constraints: at most 5 tags, each 1-20 characters. */
    @Size(max = 5, message = "at most {max} tags are allowed")
    private List<@NotBlank(message = "tag must not be blank")
                 @Size(max = 20, message = "tag must be at most {max} characters") String> tags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        this.supplierEmail = supplierEmail;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
