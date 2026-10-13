package com.example.authservice.service;

import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.entity.UserCredential;
import com.example.authservice.exception.UserAlreadyExistsException;
import com.example.authservice.repository.UserCredentialRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserCredentialRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String saveUser(RegisterRequest request) {
        String name = request.getName().trim();
        if (repository.existsByName(name)) {
            throw new UserAlreadyExistsException(name);
        }

        UserCredential credential = new UserCredential();
        credential.setName(name);
        credential.setEmail(request.getEmail().trim());
        credential.setPassword(passwordEncoder.encode(request.getPassword()));
        repository.save(credential);
        return "user added to the system";
    }

    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

}
