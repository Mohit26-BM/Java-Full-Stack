package com.company.secureapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.company.secureapi.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<AppUser> findByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    List<AppUser> findAllByOrderByUsernameAsc();

    boolean existsByUsername(String username);

    boolean existsByEmployeeId(Long employeeId);
}
