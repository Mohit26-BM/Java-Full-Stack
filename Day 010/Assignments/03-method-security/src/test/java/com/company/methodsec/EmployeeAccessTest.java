package com.company.methodsec;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.company.methodsec.repository.EmployeeRepository;
import com.company.methodsec.security.EmployeeAccess;

/** The custom authorization bean is plain Java: unit-tested without Spring. */
class EmployeeAccessTest {

    private final EmployeeAccess access = new EmployeeAccess(new EmployeeRepository());

    @Test
    void isSelf_matchesTheLoginToItsOwnEmployeeRecord() {
        assertThat(access.isSelf(4L, login("asha"))).isTrue();
        assertThat(access.isSelf(5L, login("asha"))).isFalse();
    }

    @Test
    void isManagerOf_onlyForDirectReports() {
        assertThat(access.isManagerOf(4L, login("manoj"))).isTrue();   // Asha reports to Manoj
        assertThat(access.isManagerOf(6L, login("manoj"))).isFalse();  // Meena reports to Hana
        assertThat(access.isManagerOf(4L, login("admin"))).isFalse();  // admin is Manoj's manager, not Asha's
        assertThat(access.isManagerOf(3L, login("admin"))).isTrue();
    }

    @Test
    void unknownLoginOrUnknownEmployee_isNeverAllowed() {
        assertThat(access.isSelf(4L, login("mallory"))).isFalse();
        assertThat(access.isManagerOf(999L, login("manoj"))).isFalse();
    }

    private static Authentication login(String username) {
        return UsernamePasswordAuthenticationToken.authenticated(username, null, java.util.List.of());
    }
}
