package testbank.service;

import java.util.List;

import testbank.dao.AccountDao;
import testbank.dao.CustomerDao;
import testbank.entity.Account;
import testbank.entity.Customer;
import testbank.exception.CustomerHasAccountsException;
import testbank.exception.CustomerNotFoundException;
import testbank.exception.DuplicateCustomerException;

public class CustomerServiceImpl implements CustomerService {

	private final CustomerDao customerDao;
	private final AccountDao accountDao;

	public CustomerServiceImpl(CustomerDao customerDao, AccountDao accountDao) {
	    this.customerDao = customerDao;
	    this.accountDao = accountDao;
	}

    @Override
    public void addCustomer(Customer customer)
            throws DuplicateCustomerException {

        // Basic null validation
        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer details cannot be null."
            );
        }

        // Check for duplicate customer ID
        Customer existingCustomer =
                customerDao.getCustomerById(customer.getCustomerId());

        if (existingCustomer != null) {
            throw new DuplicateCustomerException(
                    "Customer with ID "
                    + customer.getCustomerId()
                    + " already exists."
            );
        }

        boolean added = customerDao.addCustomer(customer);

        if (!added) {
            throw new IllegalStateException(
                    "Unable to add customer. Please try again."
            );
        }
    }

    @Override
    public Customer getCustomerById(int customerId)
            throws CustomerNotFoundException {

        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be greater than 0."
            );
        }

        Customer customer = customerDao.getCustomerById(customerId);

        if (customer == null) {
            throw new CustomerNotFoundException(
                    "Customer with ID "
                    + customerId
                    + " was not found."
            );
        }

        return customer;
    }

    @Override
    public List<Customer> getAllCustomers() {

        return customerDao.getAllCustomers();
    }

    @Override
    public void updateCustomer(Customer customer)
            throws CustomerNotFoundException {

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer details cannot be null."
            );
        }

        // Check whether customer exists
        Customer existingCustomer =
                customerDao.getCustomerById(customer.getCustomerId());

        if (existingCustomer == null) {
            throw new CustomerNotFoundException(
                    "Customer with ID "
                    + customer.getCustomerId()
                    + " was not found. Update cannot be performed."
            );
        }

        boolean updated = customerDao.updateCustomer(customer);

        if (!updated) {
            throw new IllegalStateException(
                    "Unable to update customer. Please try again."
            );
        }
    }

    @Override
    public void deleteCustomer(int customerId)
            throws CustomerNotFoundException, CustomerHasAccountsException {

        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be greater than 0."
            );
        }

        Customer customer = customerDao.getCustomerById(customerId);

        if (customer == null) {
            throw new CustomerNotFoundException(
                    "Customer with ID " + customerId + " was not found."
            );
        }

        for (Account account : accountDao.getAllAccounts()) {

            if (account.getCustomerId() == customerId) {
                throw new CustomerHasAccountsException(
                        "Customer cannot be deleted because the customer has an active account."
                );
            }
        }

        boolean deleted = customerDao.deleteCustomer(customerId);

        if (!deleted) {
            throw new CustomerNotFoundException(
                    "Customer with ID " + customerId + " was not found."
            );
        }
    }
}