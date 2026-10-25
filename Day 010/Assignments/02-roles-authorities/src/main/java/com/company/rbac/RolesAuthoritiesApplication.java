package com.company.rbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** HR application secured with roles (ADMIN, HR, MANAGER, USER) and permissions (EMPLOYEE_READ/WRITE/DELETE). */
@SpringBootApplication
@ConfigurationPropertiesScan
public class RolesAuthoritiesApplication {

    public static void main(String[] args) {
        SpringApplication.run(RolesAuthoritiesApplication.class, args);
    }
}
