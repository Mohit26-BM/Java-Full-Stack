package com.company.dbauth.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.dbauth.dto.UserResponses.UserResponse;
import com.company.dbauth.entity.AppUser;
import com.company.dbauth.entity.Role;
import com.company.dbauth.exception.ApiException;
import com.company.dbauth.repository.AppUserRepository;
import com.company.dbauth.repository.RoleRepository;

@Service
public class UserAdminService {
    private final AppUserRepository users;
    private final RoleRepository roles;
    private final Clock clock;

    public UserAdminService(AppUserRepository users, RoleRepository roles, Clock clock) {
        this.users = users;
        this.roles = roles;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        LocalDateTime now = LocalDateTime.now(clock);
        return users.findAllByOrderByUsernameAsc().stream().map(u -> UserResponse.of(u, now)).toList();
    }

    @Transactional
    public UserResponse setEnabled(Long id, boolean enabled, String actingUsername) {
        AppUser user = find(id);
        if (!enabled && user.getUsername().equals(actingUsername)) {
            throw ApiException.conflict("You cannot disable your own account");
        }
        user.setEnabled(enabled);
        return UserResponse.of(user, LocalDateTime.now(clock));
    }

    @Transactional
    public UserResponse unlock(Long id) {
        AppUser user = find(id);
        user.clearFailedLogins();
        return UserResponse.of(user, LocalDateTime.now(clock));
    }

    @Transactional
    public UserResponse replaceRoles(Long id, Set<String> roleNames) {
        AppUser user = find(id);
        Set<String> wanted = roleNames.stream().map(n -> n.toUpperCase(Locale.ROOT)).collect(Collectors.toSet());
        List<Role> found = roles.findByNameIn(wanted);
        if (found.size() != wanted.size()) {
            Set<String> unknown = new TreeSet<>(wanted);
            found.forEach(role -> unknown.remove(role.getName()));
            throw ApiException.badRequest("Unknown role(s): " + String.join(", ", unknown));
        }
        user.getRoles().clear();
        user.getRoles().addAll(found);
        return UserResponse.of(user, LocalDateTime.now(clock));
    }

    private AppUser find(Long id) {
        return users.findById(id).orElseThrow(() -> ApiException.notFound("User " + id + " not found"));
    }
}
