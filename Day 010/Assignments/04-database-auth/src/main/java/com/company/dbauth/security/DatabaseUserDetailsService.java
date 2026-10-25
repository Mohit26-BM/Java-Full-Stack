package com.company.dbauth.security;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.dbauth.entity.AppUser;
import com.company.dbauth.entity.Permission;
import com.company.dbauth.entity.Role;
import com.company.dbauth.repository.AppUserRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final AppUserRepository users;
    private final Clock clock;

    public DatabaseUserDetailsService(AppUserRepository users, Clock clock) {
        this.users = users;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        AppUser user = users.findWithAuthoritiesByUsername(username.toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("No user " + username));

        return new PortalUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getFullName(),
                user.isEnabled(),
                !user.isLockedAt(LocalDateTime.now(clock)),
                authoritiesOf(user));
    }

    static List<GrantedAuthority> authoritiesOf(AppUser user) {
        Set<String> roleNames = new TreeSet<>();
        Set<String> permissionNames = new TreeSet<>();
        for (Role role : user.getRoles()) {
            roleNames.add("ROLE_" + role.getName());
            for (Permission permission : role.getPermissions()) {
                permissionNames.add(permission.getName());
            }
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        permissionNames.forEach(name -> authorities.add(new SimpleGrantedAuthority(name)));
        roleNames.forEach(name -> authorities.add(new SimpleGrantedAuthority(name)));
        return authorities;
    }
}
