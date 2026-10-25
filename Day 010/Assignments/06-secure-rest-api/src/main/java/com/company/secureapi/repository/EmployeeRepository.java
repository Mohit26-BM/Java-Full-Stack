package com.company.secureapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.secureapi.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findAllByOrderByIdAsc();

    List<Employee> findByManagerIdOrderByIdAsc(Long managerId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
