package com.company.boot.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.model.Greeting;
import com.company.boot.model.GreetingRequest;
import com.company.boot.service.GreetingService;

/**
 * OPTIONAL EXTENSION: POST + ResponseEntity.
 * Greetings are stored in MySQL table a10_greetings.
 */
@RestController
@RequestMapping("/api/greetings")
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * POST /api/greetings   body: {"name":"Mohit","message":"Good morning"}
     * @RequestBody converts the JSON body into a GreetingRequest.
     * Returns 201 Created + Location header, or 400 for a blank name.
     */
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody GreetingRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Field 'name' is required"));
        }
        Greeting saved = greetingService.create(request);
        return ResponseEntity
                .created(URI.create("/api/greetings/" + saved.id()))
                .body(saved);
    }

    /** GET /api/greetings */
    @GetMapping
    public List<Greeting> findAll() {
        return greetingService.findAll();
    }

    /** GET /api/greetings/{id} -> 200 with the greeting, or 404 */
    @GetMapping("/{id}")
    public ResponseEntity<Greeting> findById(@PathVariable("id") int id) {
        return greetingService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
