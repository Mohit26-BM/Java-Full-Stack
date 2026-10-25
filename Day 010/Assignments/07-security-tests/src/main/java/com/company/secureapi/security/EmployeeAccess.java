package com.company.secureapi.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.company.secureapi.repository.EmployeeRepository;

@Component("access")
public class EmployeeAccess {
    private final EmployeeRepository employees;

    public EmployeeAccess(EmployeeRepository employees) {
        this.employees = employees;
    }

    public boolean isSelf(Long employeeId, Authentication authentication) {
        Long mine = employeeIdOf(authentication);
        return mine != null && mine.equals(employeeId);
    }

    public boolean isManagerOf(Long employeeId, Authentication authentication) {
        Long mine = employeeIdOf(authentication);
        return mine != null && employees.findById(employeeId)
                .map(e -> mine.equals(e.getManagerId()))
                .orElse(false);
    }

    private static Long employeeIdOf(Authentication authentication) {
        return authentication.getPrincipal() instanceof AuthenticatedUser user ? user.employeeId() : null;
    }
}
