package com.company.rbac.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.company.rbac.dto.EmployeeRequest;
import com.company.rbac.exception.EmployeeNotFoundException;
import com.company.rbac.model.Employee;

/**
 * Employee records in memory (thread-safe map). No security code here: in this
 * project every rule lives in SecurityConfig, at the URL level. (The
 * method-security project moves rules onto service methods.)
 */
@Service
public class EmployeeService {

    private final Map<Long, Employee> employees = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public EmployeeService() {
        Stream.of(
                new Employee(null, "Aditi Rao", "aditi.rao@company.com", "Management", "Director", null),
                new Employee(null, "Hana Kapoor", "hana.kapoor@company.com", "Human Resources", "HR Manager", 1L),
                new Employee(null, "Manoj Pillai", "manoj.pillai@company.com", "Engineering", "Engineering Manager", 1L),
                new Employee(null, "Asha Verma", "asha.verma@company.com", "Engineering", "Software Engineer", 3L),
                new Employee(null, "Ravi Iyer", "ravi.iyer@company.com", "Engineering", "Software Engineer", 3L),
                new Employee(null, "Rita Fernandes", "rita.fernandes@company.com", "Human Resources", "HR Intern", 2L),
                new Employee(null, "Karan Shah", "karan.shah@company.com", "Finance", "Accountant", 1L))
            .forEach(this::save);
    }

    public List<Employee> findAll() {
        return employees.values().stream().sorted(Comparator.comparing(Employee::id)).toList();
    }

    public Employee findById(Long id) {
        Employee employee = employees.get(id);
        if (employee == null) {
            throw new EmployeeNotFoundException(id);
        }
        return employee;
    }

    /** Direct reports of one manager. */
    public List<Employee> findTeam(Long managerId) {
        return findAll().stream().filter(e -> managerId.equals(e.managerId())).toList();
    }

    public Employee create(EmployeeRequest request) {
        return save(new Employee(null, request.name(), request.email(), request.department(),
                request.designation(), request.managerId()));
    }

    public Employee update(Long id, EmployeeRequest request) {
        findById(id);
        Employee updated = new Employee(id, request.name(), request.email(), request.department(),
                request.designation(), request.managerId());
        employees.put(id, updated);
        return updated;
    }

    public Employee transfer(Long id, String department) {
        Employee moved = findById(id).withDepartment(department);
        employees.put(id, moved);
        return moved;
    }

    public void delete(Long id) {
        if (employees.remove(id) == null) {
            throw new EmployeeNotFoundException(id);
        }
    }

    /** Head count per department, sorted by department name. */
    public Map<String, Long> headcountByDepartment() {
        return employees.values().stream()
                .collect(Collectors.groupingBy(Employee::department, TreeMap::new, Collectors.counting()));
    }

    private Employee save(Employee employee) {
        long id = nextId.getAndIncrement();
        Employee stored = new Employee(id, employee.name(), employee.email(), employee.department(),
                employee.designation(), employee.managerId());
        employees.put(id, stored);
        return stored;
    }
}
