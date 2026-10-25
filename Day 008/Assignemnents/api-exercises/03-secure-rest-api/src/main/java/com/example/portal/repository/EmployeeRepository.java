package com.example.portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.portal.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
