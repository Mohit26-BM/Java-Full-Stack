package com.company.spring.customer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.company.spring.customer.model.Customer;
import com.company.spring.customer.repository.CustomerRepository;

/*
@Service = BUSINESS layer.

Semantically: "this class holds business rules". It uses the
repository for data and adds the logic (validation, decisions).
CustomerRepository is injected through the constructor.
*/
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {

        this.customerRepository = customerRepository;

        System.out.println("   [@Service    CustomerService]    created, CustomerRepository injected");
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> getCustomersInCity(String city) {

        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be blank.");
        }

        return customerRepository.findByCity(city.trim());
    }

    public Customer getCustomer(int customerId) {

        Customer customer = customerRepository.findById(customerId);

        if (customer == null) {
            throw new IllegalArgumentException("Customer " + customerId + " was not found.");
        }

        return customer;
    }
}
