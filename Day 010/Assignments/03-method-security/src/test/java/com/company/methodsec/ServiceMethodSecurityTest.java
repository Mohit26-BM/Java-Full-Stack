package com.company.methodsec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

import com.company.methodsec.exception.EmployeeNotFoundException;
import com.company.methodsec.model.Employee;
import com.company.methodsec.repository.EmployeeRepository;
import com.company.methodsec.service.EmployeeService;

/**
 * The rules tested on the SERVICE, with no HTTP request, controller or URL
 * rule involved. @WithMockUser puts a user into the SecurityContext for one
 * test; the proxy around EmployeeService then checks the annotations.
 *
 * Employees: 1 admin, 2 hana (HR), 3 manoj (manages 4 and 5), 4 asha, 5 ravi, 6 Meena (reports to 2).
 */
@SpringBootTest
class ServiceMethodSecurityTest {

    @Autowired
    private EmployeeService service;

    @Autowired
    private EmployeeRepository repository;

    @BeforeEach
    void resetData() {
        repository.resetToSampleData();
    }

    /** No user at all (e.g. a scheduled job without a security context): every secured method refuses. */
    @Test
    void withoutAnyAuthentication_theMethodsRefuse() {
        assertThatThrownBy(() -> service.findAll())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
        assertThatThrownBy(() -> service.getSalary(4L))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    @Nested
    class GetSalary_HrAdminOrOwnRecord {

        @Test
        @WithMockUser(username = "hana", roles = "HR")
        void hrReadsAnySalary() {
            assertThat(service.getSalary(5L)).isEqualByComparingTo("92000");
        }

        @Test
        @WithMockUser(username = "asha", roles = "USER")
        void employeeReadsOwnSalary_butNotAColleagues() {
            assertThat(service.getSalary(4L)).isEqualByComparingTo("95000");                 // #id == own record
            assertThatThrownBy(() -> service.getSalary(5L)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @WithMockUser(username = "manoj", roles = "MANAGER")
        void managerCannotReadTheTeamsSalaries() {
            assertThatThrownBy(() -> service.getSalary(4L)).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class UpdateSalary_HrOrAdmin_neverOwnRecord {

        @Test
        @WithMockUser(username = "hana", roles = "HR")
        void hrUpdatesAnotherEmployeesSalary() {
            assertThat(service.updateSalary(4L, new BigDecimal("99000")).salary()).isEqualByComparingTo("99000");
        }

        @Test
        @WithMockUser(username = "hana", roles = "HR")
        void hrCannotRaiseOwnSalary() {
            assertThatThrownBy(() -> service.updateSalary(2L, new BigDecimal("999999")))
                    .isInstanceOf(AccessDeniedException.class);
            assertThat(repository.findById(2L)).map(Employee::salary).contains(new BigDecimal("140000"));  // unchanged
        }

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        void adminCannotRaiseOwnSalaryEither() {
            assertThatThrownBy(() -> service.updateSalary(1L, new BigDecimal("999999")))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @WithMockUser(username = "manoj", roles = "MANAGER")
        void managerCannotUpdateSalaries() {
            assertThatThrownBy(() -> service.updateSalary(4L, new BigDecimal("99000")))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class DeleteEmployee_SecuredAdminOnly {

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        void adminDeletes() {
            service.deleteEmployee(7L);
            assertThat(repository.findById(7L)).isEmpty();
        }

        @Test
        @WithMockUser(username = "hana", roles = "HR")
        void hrCannotDelete() {
            assertThatThrownBy(() -> service.deleteEmployee(7L)).isInstanceOf(AccessDeniedException.class);
            assertThat(repository.findById(7L)).isPresent();
        }
    }

    @Nested
    class ManagerOwnership {

        @Test
        @WithMockUser(username = "manoj", roles = "MANAGER")
        void managerChangesDirectReportsDesignation_butNoOneElses() {
            assertThat(service.updateDesignation(4L, "Senior Software Engineer").designation())
                    .isEqualTo("Senior Software Engineer");
            assertThatThrownBy(() -> service.updateDesignation(6L, "Lead"))       // Meena reports to hana
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @WithMockUser(username = "asha", roles = "USER")
        void employeeCannotChangeOwnDesignation() {
            assertThatThrownBy(() -> service.updateDesignation(4L, "CTO")).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @WithMockUser(username = "manoj", roles = "MANAGER")
        void managerListsOwnTeamOnly() {
            assertThat(service.findTeam(3L)).extracting(Employee::name).containsExactly("Asha Verma", "Ravi Iyer");
            assertThatThrownBy(() -> service.findTeam(2L)).isInstanceOf(AccessDeniedException.class);
        }

        /** The MANAGER role alone is not enough: asha passes isSelf(4) for team 4, but is no manager. */
        @Test
        @WithMockUser(username = "asha", roles = "USER")
        void nonManagerCannotListATeam_evenTheirOwnId() {
            assertThatThrownBy(() -> service.findTeam(4L)).isInstanceOf(AccessDeniedException.class);
        }
    }

    /** @PostAuthorize: the method runs FIRST, the rule checks the returned object afterwards. */
    @Nested
    @ExtendWith(OutputCaptureExtension.class)
    class PostAuthorizeExperiment {

        @Test
        @WithMockUser(username = "asha", roles = "USER")
        void ownRecordIsReturned() {
            assertThat(service.getEmployee(4L).name()).isEqualTo("Asha Verma");
        }

        @Test
        @WithMockUser(username = "manoj", roles = "MANAGER")
        void managerSeesDirectReport_butNotOthers() {
            assertThat(service.getEmployee(5L).name()).isEqualTo("Ravi Iyer");
            assertThatThrownBy(() -> service.getEmployee(6L)).isInstanceOf(AccessDeniedException.class);
        }

        /** Proof that the method body ran before the denial: its log line is printed. */
        @Test
        @WithMockUser(username = "asha", roles = "USER")
        void deniedCall_stillExecutedTheMethod(CapturedOutput output) {
            assertThatThrownBy(() -> service.getEmployee(5L)).isInstanceOf(AccessDeniedException.class);
            assertThat(output).contains("Loading employee 5");
        }

        /**
         * The side effect of "check afterwards": an unknown id fails INSIDE the method
         * (404) before any rule runs, while a colleague's id gives 403. A caller can
         * therefore tell which ids exist.
         */
        @Test
        @WithMockUser(username = "asha", roles = "USER")
        void unknownId_failsBeforeTheRuleIsChecked() {
            assertThatThrownBy(() -> service.getEmployee(999L)).isInstanceOf(EmployeeNotFoundException.class);
        }
    }
}
