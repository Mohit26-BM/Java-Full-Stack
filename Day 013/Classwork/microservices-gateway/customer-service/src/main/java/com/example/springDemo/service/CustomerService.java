package com.example.springDemo.service;

import java.util.List;

import com.example.springDemo.dto.CustomerRequest;
import com.example.springDemo.dto.CustomerResponse;
import com.example.springDemo.dto.LoanResponse;
import com.example.springDemo.dto.SavingsAccountResponse;

public interface CustomerService {

    CustomerResponse addCustomer(CustomerRequest request);

    CustomerResponse getCustomer(Long customerId);

    List<CustomerResponse> getAllCustomers();

    List<SavingsAccountResponse> getSavings(Long customerId);

    List<LoanResponse> getLoans(Long customerId);

    String getAccountServicePort();
}
