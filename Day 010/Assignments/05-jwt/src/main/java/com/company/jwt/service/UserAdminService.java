package com.company.jwt.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.jwt.dto.AuthDtos.DirectoryEntry;
import com.company.jwt.dto.AuthDtos.UserSummary;
import com.company.jwt.entity.AppUser;
import com.company.jwt.entity.Role;
import com.company.jwt.exception.ApiException;
import com.company.jwt.repository.AppUserRepository;

@Service
public class UserAdminService {
    private final AppUserRepository users;
    private final RefreshTokenService refreshTokens;

    public UserAdminService(AppUserRepository users, RefreshTokenService refreshTokens) {
        this.users = users;
        this.refreshTokens = refreshTokens;
    }

    @Transactional(readOnly = true)
    public List<UserSummary> findAll() {
        return users.findAllByOrderByUsernameAsc().stream().map(UserAdminService::summary).toList();
    }

    @Transactional(readOnly = true)
    public List<DirectoryEntry> directory() {
        return users.findByEnabledTrueOrderByFullNameAsc().stream()
                .map(u -> new DirectoryEntry(u.getFullName(), u.getEmail()))
                .toList();
    }

    @Transactional
    public UserSummary setEnabled(Long id, boolean enabled, String actingUsername) {
        AppUser user = find(id);
        if (!enabled && user.getUsername().equals(actingUsername)) {
            throw ApiException.conflict("You cannot disable your own account");
        }
        user.setEnabled(enabled);
        if (!enabled) {
            refreshTokens.revokeAll(id);
        }
        return summary(user);
    }

    @Transactional
    public int revokeTokens(Long id) {
        find(id);
        return refreshTokens.revokeAll(id);
    }

    private AppUser find(Long id) {
        return users.findById(id).orElseThrow(() -> ApiException.notFound("User " + id + " not found"));
    }

    private static UserSummary summary(AppUser user) {
        return new UserSummary(user.getId(), user.getUsername(), user.getFullName(), user.isEnabled(),
                user.getRoles().stream().map(Role::getName).sorted().toList());
    }
}
