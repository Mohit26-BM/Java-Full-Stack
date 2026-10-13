package com.example.authservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.entity.UserCredential;
import com.example.authservice.exception.UserAlreadyExistsException;
import com.example.authservice.repository.UserCredentialRepository;

class AuthServiceTest {

    private final UserCredentialRepository repository = mock(UserCredentialRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthService service = new AuthService(repository, passwordEncoder, jwtService);

    @Test
    void passwordIsStoredAsBcryptHash() {
        when(repository.existsByName("mohit")).thenReturn(false);

        service.saveUser(new RegisterRequest("mohit", "mohit@example.com", "Passw0rd!123"));

        ArgumentCaptor<UserCredential> saved = ArgumentCaptor.forClass(UserCredential.class);
        verify(repository).save(saved.capture());
        assertEquals("mohit", saved.getValue().getName());
        assertEquals(0, saved.getValue().getId());
        assertNotEquals("Passw0rd!123", saved.getValue().getPassword());
        assertTrue(passwordEncoder.matches("Passw0rd!123", saved.getValue().getPassword()));
    }

    @Test
    void duplicateNameIsRejectedAndNothingIsSaved() {
        when(repository.existsByName("mohit")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> service.saveUser(new RegisterRequest("mohit", "other@example.com", "Passw0rd!123")));

        verify(repository, never()).save(any());
    }
}
