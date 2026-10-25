package com.example.hospital.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hospital.config.CacheConfig;
import com.example.hospital.dto.DepartmentRequest;
import com.example.hospital.dto.DepartmentResponse;
import com.example.hospital.entity.Department;
import com.example.hospital.exception.DepartmentNotFoundException;
import com.example.hospital.exception.DuplicateDepartmentException;
import com.example.hospital.mapper.DepartmentMapper;
import com.example.hospital.repository.DepartmentRepository;

/**
 * Module A - Department management.
 * Class level: every method runs in a READ-ONLY transaction;
 * methods that change data override it with a normal @Transactional.
 */
@Service
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.DEPARTMENTS, allEntries = true)
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        String name = request.name().trim();
        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateDepartmentException(name);
        }
        Department saved = departmentRepository.save(new Department(name, request.location()));
        return DepartmentMapper.toResponse(saved);
    }

    /** No save() call: a loaded entity is "managed", so Hibernate writes its changes at commit. */
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.DEPARTMENTS, allEntries = true)
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = getDepartment(id);
        String name = request.name().trim();
        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateDepartmentException(name);
        }
        department.setName(name);
        department.setLocation(request.location());
        return DepartmentMapper.toResponse(department);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.DEPARTMENTS, allEntries = true)
    public DepartmentResponse activateDepartment(Long id) {
        Department department = getDepartment(id);
        department.setActive(true);
        return DepartmentMapper.toResponse(department);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.DEPARTMENTS, allEntries = true)
    public DepartmentResponse deactivateDepartment(Long id) {
        Department department = getDepartment(id);
        department.setActive(false);
        return DepartmentMapper.toResponse(department);
    }

    /**
     * All departments, or only the active ones. Cached (one entry per activeOnly value):
     * the second call with the same argument returns the stored list without a SELECT.
     */
    @Cacheable(cacheNames = CacheConfig.DEPARTMENTS, key = "#activeOnly")
    public List<DepartmentResponse> findDepartments(boolean activeOnly) {
        List<Department> departments = activeOnly
                ? departmentRepository.findByActiveTrueOrderByNameAsc()
                : departmentRepository.findAll(Sort.by("name"));
        return departments.stream().map(DepartmentMapper::toResponse).toList();
    }

    public DepartmentResponse findById(Long id) {
        return DepartmentMapper.toResponse(getDepartment(id));
    }

    public DepartmentResponse findByName(String name) {
        Department department = departmentRepository.findByNameIgnoreCase(name.trim())
                .orElseThrow(() -> new DepartmentNotFoundException(name));
        return DepartmentMapper.toResponse(department);
    }

    private Department getDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
    }
}
