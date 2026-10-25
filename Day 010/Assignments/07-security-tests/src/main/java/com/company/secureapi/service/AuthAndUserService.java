package com.company.secureapi.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.secureapi.dto.Dtos.CreateUserRequest;
import com.company.secureapi.dto.Dtos.TokenResponse;
import com.company.secureapi.dto.Dtos.UserResponse;
import com.company.secureapi.entity.AppUser;
import com.company.secureapi.exception.ApiException;
import com.company.secureapi.exception.ErrorCode;
import com.company.secureapi.repository.AppUserRepository;
import com.company.secureapi.repository.EmployeeRepository;
import com.company.secureapi.repository.RoleRepository;
import com.company.secureapi.security.JwtService;

@Service
public class AuthAndUserService {
    private static final Logger auditLog = LoggerFactory.getLogger("security.audit");

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserRepository users;
    private final RoleRepository roles;
    private final EmployeeRepository employees;
    private final PasswordEncoder passwordEncoder;

    public AuthAndUserService(AuthenticationManager authenticationManager, JwtService jwtService, AppUserRepository users,
                              RoleRepository roles, EmployeeRepository employees, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.users = users;
        this.roles = roles;
        this.employees = employees;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public TokenResponse login(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(username, password));
        Long employeeId = users.findByUsername(auth.getName()).orElseThrow().getEmployeeId();
        String token = jwtService.issue(auth.getName(), employeeId,
                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().toList());
        return new TokenResponse("Bearer", token, jwtService.ttlSeconds());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return users.findAllByOrderByUsernameAsc().stream().map(AuthAndUserService::response).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (users.existsByUsername(request.username())) {
            throw ApiException.duplicate("Username " + request.username() + " is already taken");
        }
        if (!employees.existsById(request.employeeId())) {
            throw ApiException.employeeNotFound(request.employeeId());
        }
        if (users.existsByEmployeeId(request.employeeId())) {
            throw ApiException.duplicate("Employee " + request.employeeId() + " already has a login");
        }
        AppUser user = new AppUser(request.username(), passwordEncoder.encode(request.password()), request.employeeId());
        user.getRoles().add(roles.findByName(request.role())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED, "Unknown role")));
        UserResponse created = response(users.save(user));
        auditLog.info("audit_event=USER_CREATED username={} role={} employee={}", request.username(), request.role(), request.employeeId());
        return created;
    }

    private static UserResponse response(AppUser user) {
        return new UserResponse(user.getId(), user.getUsername(), user.isEnabled(), user.getEmployeeId(),
                user.getRoles().stream().map(r -> r.getName()).sorted().toList());
    }
}
