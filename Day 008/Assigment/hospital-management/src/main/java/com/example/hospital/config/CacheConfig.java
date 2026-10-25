package com.example.hospital.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Extension: caching of reference data.
 *
 * Departments and the doctor summary list are read on almost every screen but
 * change rarely, so their results are kept in memory (Spring's default
 * ConcurrentMapCacheManager) instead of querying MySQL every time.
 *
 *   @Cacheable  - DepartmentService.findDepartments, DoctorService.findActiveDoctorSummaries
 *   @CacheEvict - every method that creates or changes a department or doctor
 *                 empties the cache, so the next read goes to the database again.
 *
 * Appointments are NOT cached: they change all the time and must always be current.
 *
 * order: the cache advice runs OUTSIDE the transaction advice (a smaller order
 * number wraps a larger one; transactions use LOWEST_PRECEDENCE). So @CacheEvict
 * empties the cache only after the transaction has committed, and a read in
 * between cannot put the old data straight back into the cache.
 */
@Configuration
@EnableCaching(order = Ordered.LOWEST_PRECEDENCE - 1)
public class CacheConfig {

    public static final String DEPARTMENTS = "departments";
    public static final String DOCTOR_SUMMARIES = "doctorSummaries";
}
