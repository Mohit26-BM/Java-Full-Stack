package com.company.boot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.company.boot.model.Greeting;
import com.company.boot.model.GreetingRequest;
import com.company.boot.repository.GreetingRepository;

@Service
public class GreetingService {

    private static final String DEFAULT_MESSAGE = "Welcome!";

    private final GreetingRepository greetingRepository;

    public GreetingService(GreetingRepository greetingRepository) {
        this.greetingRepository = greetingRepository;
    }

    /** Saves a greeting; a missing/blank message gets a default. */
    public Greeting create(GreetingRequest request) {
        String message = (request.message() == null || request.message().isBlank())
                ? DEFAULT_MESSAGE
                : request.message().trim();
        int id = greetingRepository.insert(request.name().trim(), message);
        return greetingRepository.findById(id).orElseThrow();
    }

    public List<Greeting> findAll() {
        return greetingRepository.findAll();
    }

    public Optional<Greeting> findById(int id) {
        return greetingRepository.findById(id);
    }
}
