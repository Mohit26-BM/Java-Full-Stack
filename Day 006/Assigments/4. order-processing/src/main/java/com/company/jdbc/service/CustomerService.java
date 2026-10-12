package com.company.jdbc.service;

import java.util.List;

import com.company.jdbc.exception.CustomerNotFoundException;
import com.company.jdbc.exception.DuplicateCustomerException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Customer;

public interface CustomerService {

    long addCustomer(String name, String email)
            throws InvalidInputException,
                   DuplicateCustomerException;

    Customer getCustomer(long customerId)
            throws CustomerNotFoundException;

    List<Customer> getAllCustomers();
}
