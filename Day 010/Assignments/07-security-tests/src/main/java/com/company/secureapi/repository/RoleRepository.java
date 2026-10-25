package com.company.secureapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.secureapi.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
