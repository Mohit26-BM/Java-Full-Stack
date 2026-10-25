package com.company.boot.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Profile("!prod") - registered in EVERY profile EXCEPT prod.
 * A debug endpoint that must never be reachable in production.
 */
@RestController
@Profile("!prod")
public class DebugController {

    private final Environment environment;

    public DebugController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/api/debug")
    public Map<String, Object> debug() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("warning", "debug endpoint - not available in prod");
        body.put("javaVersion", System.getProperty("java.version"));
        body.put("appEnvironment", environment.getProperty("app.environment"));
        return body;
    }
}
