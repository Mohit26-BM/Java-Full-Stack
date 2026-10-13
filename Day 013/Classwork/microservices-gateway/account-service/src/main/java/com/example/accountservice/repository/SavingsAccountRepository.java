package com.example.accountservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.accountservice.entity.SavingsAccount;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {

    List<SavingsAccount> findByCustomerId(Long customerId);
}
