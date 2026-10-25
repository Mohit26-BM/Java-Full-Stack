package com.company.security.model;

/** An employee record (kept in memory; databases come in a later project). */
public record Employee(Long id, String name, String department, String designation) {
}
