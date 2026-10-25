package com.company.methodsec.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.company.methodsec.repository.EmployeeRepository;

/**
 * Custom authorization bean, called from the annotations by its bean name:
 *
 *     @PreAuthorize("@employeeAccess.isSelf(#id, authentication)")
 *
 * Ownership rules need data (which employee belongs to which login, who manages
 * whom). Writing them as Java methods keeps the annotations short, and the
 * rules can be unit-tested and reused.
 *
 * It reads the UNSECURED repository on purpose: calling the secured service from
 * here would run the method rules again, inside a rule.
 */
@Component("employeeAccess")
public class EmployeeAccess {

    private final EmployeeRepository repository;

    public EmployeeAccess(EmployeeRepository repository) {
        this.repository = repository;
    }

    /** Is employee {@code employeeId} the logged-in user's own record? */
    public boolean isSelf(Long employeeId, Authentication authentication) {
        return repository.findByUsername(authentication.getName())
                .map(me -> me.id().equals(employeeId))
                .orElse(false);
    }

    /** Is the logged-in user the direct line manager of employee {@code employeeId}? */
    public boolean isManagerOf(Long employeeId, Authentication authentication) {
        return repository.findByUsername(authentication.getName())
                .flatMap(me -> repository.findById(employeeId)
                        .map(target -> me.id().equals(target.managerId())))
                .orElse(false);
    }
}
