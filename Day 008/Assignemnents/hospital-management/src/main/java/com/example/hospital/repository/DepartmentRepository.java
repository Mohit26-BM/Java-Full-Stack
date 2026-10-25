package com.example.hospital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hospital.entity.Department;

/**
 * JpaRepository<Department, Long> already provides save, findById, findAll,
 * count, delete ... The methods below are DERIVED QUERIES: Spring Data reads
 * the method name and builds the SQL.
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /** ... WHERE upper(name) = upper(?) */
    Optional<Department> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    /** Duplicate check on update: same name, but a different department. */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /** ... WHERE active = true ORDER BY name ASC */
    List<Department> findByActiveTrueOrderByNameAsc();
}
