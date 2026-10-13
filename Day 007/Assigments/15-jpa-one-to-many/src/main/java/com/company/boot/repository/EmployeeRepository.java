package com.company.boot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.company.boot.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmailIgnoreCase(String email);

    /** Derived query on a nested property: WHERE department_id = ? */
    List<Employee> findByDepartmentIdOrderByIdAsc(Long departmentId);

    /** All employees WITH their department in one query (JOIN) - used for the LAZY/EAGER comparison. */
    @EntityGraph(attributePaths = "department")
    List<Employee> findAllByOrderByIdAsc();

    /** All employees WITHOUT the department (it stays a lazy proxy). */
    List<Employee> findByOrderByIdAsc();
}
