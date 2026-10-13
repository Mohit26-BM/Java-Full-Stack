package com.company.boot.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.config.AppSettings;
import com.company.boot.notification.NotificationSender;

/**
 * Exposes the ACTIVE configuration.
 * The same code returns different values depending on the active profile.
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final Environment environment;
    private final AppSettings appSettings;
    private final NotificationSender notificationSender;

    public ProfileController(Environment environment,
                             AppSettings appSettings,
                             NotificationSender notificationSender) {
        this.environment = environment;
        this.appSettings = appSettings;
        this.notificationSender = notificationSender;
    }

    @GetMapping
    public Map<String, Object> activeConfiguration() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("activeProfiles", Arrays.asList(environment.getActiveProfiles()));
        body.put("defaultProfiles", Arrays.asList(environment.getDefaultProfiles()));
        body.put("message", appSettings.getMessage());
        body.put("environment", appSettings.getEnvironment());
        body.put("showSqlLogs", appSettings.isShowSqlLogs());
        body.put("cacheTtlSeconds", appSettings.getCacheTtlSeconds());
        body.put("serverPort", environment.getProperty("local.server.port"));
        body.put("notificationSender", notificationSender.getClass().getSimpleName());
        return body;
    }

    @GetMapping("/notify")
    public Map<String, Object> notifyUser(@RequestParam(defaultValue = "customer@example.com") String to) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("result", notificationSender.send(to, appSettings.getMessage()));
        return body;
    }
}
