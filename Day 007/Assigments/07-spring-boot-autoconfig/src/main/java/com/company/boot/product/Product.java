package com.company.boot.product;

import java.math.BigDecimal;

/*
A Java record (Java 16+): an immutable data class. Jackson, configured
automatically by spring-boot-starter-web, turns it into JSON.
*/
public record Product(int productId, String productName, String category, BigDecimal price) {
}
