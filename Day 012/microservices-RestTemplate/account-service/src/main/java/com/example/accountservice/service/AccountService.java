package com.example.accountservice.service;

import java.util.List;

import com.example.accountservice.dto.LoanRequest;
import com.example.accountservice.dto.LoanResponse;
import com.example.accountservice.dto.SavingsAccountRequest;
import com.example.accountservice.dto.SavingsAccountResponse;

public interface AccountService {

    SavingsAccountResponse openSavingsAccount(SavingsAccountRequest request);

    List<SavingsAccountResponse> getSavingsAccounts(Long customerId);

    LoanResponse createLoan(LoanRequest request);

    List<LoanResponse> getLoans(Long customerId);
}
