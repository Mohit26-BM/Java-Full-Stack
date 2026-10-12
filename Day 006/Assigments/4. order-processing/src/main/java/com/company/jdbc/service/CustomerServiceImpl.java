package com.company.jdbc.service;

import java.util.List;

import com.company.jdbc.dao.CustomerDao;
import com.company.jdbc.exception.CustomerNotFoundException;
import com.company.jdbc.exception.DuplicateCustomerException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Customer;

public class CustomerServiceImpl implements CustomerService {

    private static final String NAME_PATTERN = "[A-Za-z .'-]+";

    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$";

    private final CustomerDao customerDao;

    public CustomerServiceImpl(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public long addCustomer(String name, String email)
            throws InvalidInputException,
                   DuplicateCustomerException {

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Customer name cannot be blank.");
        }

        String cleanName = name.trim();

        if (cleanName.length() > 120 || !cleanName.matches(NAME_PATTERN)) {
            throw new InvalidInputException(
                    "Customer name must be up to 120 letters, spaces, "
                    + "dots, hyphens or apostrophes."
            );
        }

        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email cannot be blank.");
        }

        String cleanEmail = email.trim().toLowerCase();

        if (cleanEmail.length() > 120 || !cleanEmail.matches(EMAIL_PATTERN)) {
            throw new InvalidInputException("Invalid email address: " + email.trim());
        }

        return customerDao.add(new Customer(cleanName, cleanEmail));
    }

    @Override
    public Customer getCustomer(long customerId)
            throws CustomerNotFoundException {

        Customer customer = customerDao.findById(customerId);

        if (customer == null) {
            throw new CustomerNotFoundException(
                    "Customer with ID " + customerId + " was not found."
            );
        }

        return customer;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerDao.findAll();
    }
}
