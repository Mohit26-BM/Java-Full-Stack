package com.company.dbauth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.company.dbauth.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Used at every login. The entity graph loads the user, the roles AND each
     * role's permissions in ONE query (joins), instead of one extra query per role.
     */
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<AppUser> findWithAuthoritiesByUsername(String username);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "roles")
    List<AppUser> findAllByOrderByUsernameAsc();

    List<AppUser> findByEnabledTrueOrderByFullNameAsc();
}
