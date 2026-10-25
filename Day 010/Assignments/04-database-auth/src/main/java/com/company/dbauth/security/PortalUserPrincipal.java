package com.company.dbauth.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * What Spring Security keeps about a logged-in user: an immutable COPY of the
 * database row, made by DatabaseUserDetailsService. It is detached from JPA, so
 * nothing is lazily loaded later and the entity never leaves the service layer.
 *
 * The flags are read by DaoAuthenticationProvider during login:
 *   isEnabled() false        -> DisabledException (401 "Account is disabled")
 *   isAccountNonLocked() false -> LockedException (401 "Account is temporarily locked")
 */
public final class PortalUserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String passwordHash;
    private final String fullName;
    private final boolean enabled;
    private final boolean accountNonLocked;
    private final List<GrantedAuthority> authorities;

    public PortalUserPrincipal(Long id, String username, String passwordHash, String fullName,
                               boolean enabled, boolean accountNonLocked, List<GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.authorities = List.copyOf(authorities);
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public String toString() {
        return "PortalUserPrincipal[" + username + ", " + authorities + "]";     // never prints the hash
    }
}
