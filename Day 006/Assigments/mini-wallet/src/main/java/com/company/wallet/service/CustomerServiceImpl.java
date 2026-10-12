package com.company.wallet.service;

import java.util.List;

import com.company.wallet.dao.CustomerDao;
import com.company.wallet.dao.WalletDao;
import com.company.wallet.exception.CustomerNotFoundException;
import com.company.wallet.exception.WalletException;
import com.company.wallet.model.Customer;
import com.company.wallet.model.Wallet;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerDao customerDao;
    private final WalletDao walletDao;
    private final TransactionManager transactionManager;

    public CustomerServiceImpl(CustomerDao customerDao,
                               WalletDao walletDao,
                               TransactionManager transactionManager) {

        this.customerDao = customerDao;
        this.walletDao = walletDao;
        this.transactionManager = transactionManager;
    }

    @Override
    public Customer register(String fullName, String email, String phone)
            throws WalletException {

        Customer customer = new Customer(
                InputRules.name(fullName),
                InputRules.email(email),
                InputRules.phone(phone)
        );

        /*
        No "SELECT first, then INSERT" check: two requests could both
        pass such a check. The UNIQUE constraints are the real guard;
        the DAO turns a duplicate-key error into DuplicateCustomerException.
        */
        return transactionManager.inTransaction(connection -> {

            customer.setCustomerId(customerDao.insert(connection, customer));

            return customer;
        });
    }

    @Override
    public Customer findById(long customerId) throws WalletException {

        Customer customer = customerDao.findById(customerId);

        if (customer == null) {
            throw new CustomerNotFoundException("Customer " + customerId + " was not found.");
        }

        return customer;
    }

    @Override
    public Customer findByEmail(String email) throws WalletException {

        String cleanEmail = InputRules.email(email);

        Customer customer = customerDao.findByEmail(cleanEmail);

        if (customer == null) {
            throw new CustomerNotFoundException("No customer with email " + cleanEmail + ".");
        }

        return customer;
    }

    @Override
    public List<Customer> findAll() {
        return customerDao.findAll();
    }

    @Override
    public List<Wallet> walletsOf(long customerId) {
        return walletDao.findByCustomer(customerId);
    }
}
