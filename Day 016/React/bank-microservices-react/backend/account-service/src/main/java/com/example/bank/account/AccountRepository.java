package com.example.bank.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Loads the account and locks its row until the transaction ends (SELECT ... FOR UPDATE), so two requests
     * cannot both read the same balance and both spend it. Must be called inside a transaction.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountno = :accountno")
    Optional<Account> findForUpdate(@Param("accountno") Long accountno);

    List<Account> findByCustomerId(Long customerId);

    List<Account> findByStatus(AccountStatus status);
}
