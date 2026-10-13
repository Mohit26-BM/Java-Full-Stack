package com.company.boot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.company.boot.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByNameIgnoreCase(String name);

    /**
     * @EntityGraph loads the LAZY employees collection in the SAME query (LEFT JOIN),
     * avoiding the N+1 problem when every department's employees are needed.
     */
    @EntityGraph(attributePaths = "employees")
    @Query("SELECT d FROM Department d ORDER BY d.id")
    List<Department> findAllWithEmployees();

    @EntityGraph(attributePaths = "employees")
    Optional<Department> findWithEmployeesById(Long id);
}
