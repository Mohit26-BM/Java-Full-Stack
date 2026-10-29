package com.example.bank.customer;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Who is calling, taken from the verified token ({@link JwtAuthFilter} puts the claims on the request).
 * If a request has no role (for example an old token from before roles existed) nothing is allowed: it fails closed.
 */
public record Caller(String username, String role, Long customerId) {

    public static Caller from(HttpServletRequest request) {
        Object username = request.getAttribute("username");
        Object role = request.getAttribute("role");
        Object customerId = request.getAttribute("customerId");
        return new Caller(username == null ? "unknown" : username.toString(),
                role == null ? null : role.toString(),
                customerId instanceof Number n ? n.longValue() : null);
    }

    /** Tellers and admins work with any customer. */
    public boolean isStaff() {
        return "TELLER".equals(role) || "ADMIN".equals(role);
    }

    /** A customer login linked to exactly this customer record. */
    public boolean owns(Long customerRecordId) {
        return "CUSTOMER".equals(role) && customerId != null && customerId.equals(customerRecordId);
    }
}
