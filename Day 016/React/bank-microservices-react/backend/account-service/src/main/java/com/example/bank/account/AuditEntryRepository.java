package com.example.bank.account;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEntryRepository extends JpaRepository<AuditEntry, Long> {

    /** Newest first. */
    List<AuditEntry> findAllByOrderByIdDesc(Pageable pageable);
}
