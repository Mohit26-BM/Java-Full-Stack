package com.company.wallet.service;

import java.util.List;

import com.company.wallet.exception.WalletException;
import com.company.wallet.model.Customer;
import com.company.wallet.model.Wallet;

public interface CustomerService {

    /*
    Throws ValidationException or DuplicateCustomerException.
    */
    Customer register(String fullName, String email, String phone) throws WalletException;

    /*
    Throws CustomerNotFoundException.
    */
    Customer findById(long customerId) throws WalletException;

    Customer findByEmail(String email) throws WalletException;

    List<Customer> findAll();

    List<Wallet> walletsOf(long customerId);
}
