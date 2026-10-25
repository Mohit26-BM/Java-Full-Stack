package com.company.jwt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.company.jwt.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<AppUser> findWithAuthoritiesByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<AppUser> findWithAuthoritiesById(Long id);

    @EntityGraph(attributePaths = "roles")
    List<AppUser> findAllByOrderByUsernameAsc();

    List<AppUser> findByEnabledTrueOrderByFullNameAsc();
}
