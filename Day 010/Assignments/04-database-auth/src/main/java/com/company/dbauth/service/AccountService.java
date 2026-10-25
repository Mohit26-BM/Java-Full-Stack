package com.company.dbauth.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.dbauth.dto.AuthRequests.PasswordChangeRequest;
import com.company.dbauth.dto.AuthRequests.RegistrationRequest;
import com.company.dbauth.dto.UserResponses.DirectoryEntry;
import com.company.dbauth.dto.UserResponses.UserResponse;
import com.company.dbauth.entity.AppUser;
import com.company.dbauth.exception.ApiException;
import com.company.dbauth.repository.AppUserRepository;
import com.company.dbauth.repository.RoleRepository;

@Service
public class AccountService {
    static final String DEFAULT_ROLE = "USER";

    private final AppUserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public AccountService(AppUserRepository users, RoleRepository roles, PasswordEncoder passwordEncoder, Clock clock) {
        this.users = users;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Transactional
    public UserResponse register(RegistrationRequest request) {
        String username = request.username().toLowerCase(Locale.ROOT);
        String email = request.email().toLowerCase(Locale.ROOT);
        if (users.existsByUsername(username)) {
            throw ApiException.conflict("Username '" + username + "' is already taken");
        }
        if (users.existsByEmail(email)) {
            throw ApiException.conflict("Email '" + email + "' is already registered");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        AppUser user = new AppUser(username, email, request.fullName().trim(),
                passwordEncoder.encode(request.password()), now);
        user.getRoles().add(roles.findByName(DEFAULT_ROLE).orElseThrow());
        try {
            users.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw ApiException.conflict("Username or email is already registered");
        }
        return UserResponse.of(user, now);
    }

    @Transactional
    public void changePassword(String username, PasswordChangeRequest request) {
        AppUser user = users.findByUsername(username).orElseThrow();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw ApiException.badRequest("Current password is incorrect");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw ApiException.badRequest("New password must be different from the current one");
        }
        user.changePassword(passwordEncoder.encode(request.newPassword()), LocalDateTime.now(clock));
    }

    @Transactional(readOnly = true)
    public List<DirectoryEntry> directory() {
        return users.findByEnabledTrueOrderByFullNameAsc().stream()
                .map(u -> new DirectoryEntry(u.getFullName(), u.getEmail()))
                .toList();
    }
}
