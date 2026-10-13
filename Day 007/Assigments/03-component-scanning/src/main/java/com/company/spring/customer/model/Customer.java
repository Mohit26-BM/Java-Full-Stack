package com.company.spring.customer.model;

/*
Plain data class - no stereotype, so it is NOT a bean
(even though it sits inside the scanned package).
*/
public class Customer {

    private final int customerId;
    private final String customerName;
    private final String email;
    private final String city;

    public Customer(int customerId, String customerName, String email, String city) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;
        this.city = city;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return String.format("%-4d %-15s %-28s %s", customerId, customerName, email, city);
    }
}
