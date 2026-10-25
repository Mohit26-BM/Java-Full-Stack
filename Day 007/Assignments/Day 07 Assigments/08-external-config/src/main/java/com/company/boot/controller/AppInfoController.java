package com.company.boot.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.config.AppProperties;
import com.company.boot.config.AppValueSettings;

/**
 * Exposes the external configuration through REST.
 * No property value is written in Java - everything comes from
 * application*.properties, command-line arguments or environment variables.
 */
@RestController
@RequestMapping("/api/app-info")
public class AppInfoController {

    private final AppValueSettings valueSettings;
    private final AppProperties appProperties;
    private final Environment environment;

    public AppInfoController(AppValueSettings valueSettings,
                             AppProperties appProperties,
                             Environment environment) {
        this.valueSettings = valueSettings;
        this.appProperties = appProperties;
        this.environment = environment;
    }

    /** Values bound with @ConfigurationProperties. */
    @GetMapping
    public Map<String, Object> fromConfigurationProperties() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("source", "@ConfigurationProperties(prefix = \"app\")");
        body.put("name", appProperties.getName());
        body.put("version", appProperties.getVersion());
        body.put("owner", appProperties.getOwner());
        body.put("maxUsers", appProperties.getMaxUsers());
        body.put("environment", appProperties.getEnvironment());
        body.put("activeProfiles", Arrays.asList(environment.getActiveProfiles()));
        return body;
    }

    /** The same values injected with @Value. */
    @GetMapping("/value")
    public Map<String, Object> fromValue() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("source", "@Value(\"${...}\")");
        body.put("name", valueSettings.getName());
        body.put("version", valueSettings.getVersion());
        body.put("owner", valueSettings.getOwner());
        body.put("supportEmail", valueSettings.getSupportEmail());
        return body;
    }
}
