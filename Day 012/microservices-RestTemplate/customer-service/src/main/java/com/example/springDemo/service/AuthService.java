package com.example.springDemo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springDemo.dto.AuthResponse;
import com.example.springDemo.dto.CustomerResponse;
import com.example.springDemo.dto.LoginRequest;
import com.example.springDemo.dto.RegisterRequest;
import com.example.springDemo.entity.Role;
import com.example.springDemo.entity.UserAccount;
import com.example.springDemo.exception.DuplicateEmailException;
import com.example.springDemo.exception.InvalidCredentialsException;
import com.example.springDemo.repository.UserRepository;
import com.example.springDemo.security.JwtService;

@Service
public class AuthService {

    private final UserRepository users;
    private final CustomerService customers;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, CustomerService customers, PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.users = users;
        this.customers = customers;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException();
        }
        CustomerResponse customer = customers.addCustomer(request);

        UserAccount user = new UserAccount();
        user.setEmail(customer.email());
        user.setName(customer.name());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setCustomerId(customer.customerId());
        return toResponse(users.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        UserAccount user = users.findByEmailIgnoreCase(request.email().trim())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return toResponse(user);
    }

    private AuthResponse toResponse(UserAccount user) {
        return new AuthResponse(jwtService.issue(user), user.getEmail(), user.getName(), user.getRole(),
                user.getCustomerId());
    }
}
