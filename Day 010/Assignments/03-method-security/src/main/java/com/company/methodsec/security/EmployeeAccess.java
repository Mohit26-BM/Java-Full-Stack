package com.company.methodsec.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.company.methodsec.repository.EmployeeRepository;

@Component("employeeAccess")
public class EmployeeAccess {
    private final EmployeeRepository repository;

    public EmployeeAccess(EmployeeRepository repository) {
        this.repository = repository;
    }

    public boolean isSelf(Long employeeId, Authentication authentication) {
        return repository.findByUsername(authentication.getName())
                .map(me -> me.id().equals(employeeId))
                .orElse(false);
    }

    public boolean isManagerOf(Long employeeId, Authentication authentication) {
        return repository.findByUsername(authentication.getName())
                .flatMap(me -> repository.findById(employeeId)
                        .map(target -> me.id().equals(target.managerId())))
                .orElse(false);
    }
}
