package com.example.bank.customer;

import java.time.LocalDate;

public record CustomerResponse(Long customerId, String name, String email, LocalDate dateOfBirth, String gender,
                               String city) {

    static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.getCustomerId(), c.getName(), c.getEmail(), c.getDateOfBirth(), c.getGender(),
                c.getCity());
    }
}
