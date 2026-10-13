package com.company.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.boot.entity.Account;

/**
 * Note: every Spring Data repository method (save, findById ...) is itself
 * @Transactional. Called OUTSIDE a service transaction, each call therefore
 * commits on its own - that is what the "without @Transactional" demo shows.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
}
