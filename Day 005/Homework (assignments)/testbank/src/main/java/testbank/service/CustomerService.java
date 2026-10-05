package testbank.service;

import java.util.List;

import testbank.entity.Customer;
import testbank.exception.CustomerHasAccountsException;
import testbank.exception.CustomerNotFoundException;
import testbank.exception.DuplicateCustomerException;

public interface CustomerService {

    void addCustomer(Customer customer)
            throws DuplicateCustomerException;

    Customer getCustomerById(int customerId)
            throws CustomerNotFoundException;

    List<Customer> getAllCustomers();

    void updateCustomer(Customer customer)
            throws CustomerNotFoundException;

    void deleteCustomer(int customerId)
            throws CustomerNotFoundException, CustomerHasAccountsException;
}