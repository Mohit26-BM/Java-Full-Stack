package com.example.bank.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

    /** Newest first. {@code from} is inclusive and {@code to} exclusive; either may be null for "no limit". */
    @Query("select t from AccountTransaction t where t.accountno = :accountno "
            + "and (:from is null or t.createdAt >= :from) and (:to is null or t.createdAt < :to) order by t.id desc")
    List<AccountTransaction> findStatement(@Param("accountno") Long accountno, @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to, Pageable pageable);

    Optional<AccountTransaction> findByIdempotencyKey(String idempotencyKey);

    /** Money that left the account since the given time (used for the daily limit). */
    @Query("select coalesce(sum(t.amount), 0) from AccountTransaction t "
            + "where t.accountno = :accountno and t.type in :types and t.createdAt >= :since")
    BigDecimal sumSince(@Param("accountno") Long accountno, @Param("types") Collection<TransactionType> types,
                        @Param("since") LocalDateTime since);
}
