package com.company.boot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Approach 2: @Value.
 *
 * Each property is injected one by one through a ${placeholder}.
 * - No default:   ${app.owner}              -> startup FAILS if the key is missing
 * - With default: ${app.support-email:...}  -> default used if the key is missing
 * - No validation: a blank value is accepted as "".
 */
@Component
public class AppValueSettings {

    private final String name;
    private final String version;
    private final String owner;
    private final String supportEmail;

    public AppValueSettings(@Value("${app.name}") String name,
                            @Value("${app.version}") String version,
                            @Value("${app.owner}") String owner,
                            @Value("${app.support-email:support@company.com}") String supportEmail) {
        this.name = name;
        this.version = version;
        this.owner = owner;
        this.supportEmail = supportEmail;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getOwner() {
        return owner;
    }

    public String getSupportEmail() {
        return supportEmail;
    }
}
