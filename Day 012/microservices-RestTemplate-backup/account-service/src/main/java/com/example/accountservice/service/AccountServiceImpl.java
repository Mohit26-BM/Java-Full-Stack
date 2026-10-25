package com.example.accountservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.accountservice.dto.LoanRequest;
import com.example.accountservice.dto.LoanResponse;
import com.example.accountservice.dto.SavingsAccountRequest;
import com.example.accountservice.dto.SavingsAccountResponse;
import com.example.accountservice.entity.Loan;
import com.example.accountservice.entity.SavingsAccount;
import com.example.accountservice.repository.LoanRepository;
import com.example.accountservice.repository.SavingsAccountRepository;

@Service
public class AccountServiceImpl implements AccountService {

    private static final BigDecimal SAVINGS_INTEREST_RATE = new BigDecimal("3.50");

    private final SavingsAccountRepository savingsRepository;
    private final LoanRepository loanRepository;

    public AccountServiceImpl(SavingsAccountRepository savingsRepository, LoanRepository loanRepository) {
        this.savingsRepository = savingsRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    @Transactional
    public SavingsAccountResponse openSavingsAccount(SavingsAccountRequest request) {
        SavingsAccount account = new SavingsAccount();
        account.setAccountNumber(generateAccountNumber());
        account.setCustomerId(request.customerId());
        account.setBalance(request.initialDeposit().setScale(2, RoundingMode.HALF_UP));
        account.setInterestRate(SAVINGS_INTEREST_RATE);
        account.setOpenedOn(LocalDate.now());

        return SavingsAccountResponse.from(savingsRepository.save(account));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavingsAccountResponse> getSavingsAccounts(Long customerId) {
        return savingsRepository.findByCustomerId(customerId)
                .stream()
                .map(SavingsAccountResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public LoanResponse createLoan(LoanRequest request) {
        Loan loan = new Loan();
        loan.setCustomerId(request.customerId());
        loan.setLoanType(request.loanType());
        loan.setPrincipalAmount(request.principalAmount().setScale(2, RoundingMode.HALF_UP));
        loan.setInterestRate(request.interestRate().setScale(2, RoundingMode.HALF_UP));
        loan.setTenureMonths(request.tenureMonths());
        loan.setStartDate(LocalDate.now());

        return LoanResponse.from(loanRepository.save(loan));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getLoans(Long customerId) {
        return loanRepository.findByCustomerId(customerId)
                .stream()
                .map(LoanResponse::from)
                .toList();
    }

    private String generateAccountNumber() {
        return "SAV" + ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
    }
}
