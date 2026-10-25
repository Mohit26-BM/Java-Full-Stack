package com.example.hospital.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.hospital.config.CacheConfig;
import com.example.hospital.dto.DepartmentResponse;
import com.example.hospital.dto.DoctorSummary;

/** Extension: reference data is cached, and every change empties the cache. */
class CachingIntegrationTest extends IntegrationTestBase {

    @Test
    void activeDepartmentsAreCached_andCreatingADepartmentEvictsThem() {
        createDepartment("Cardiology");

        List<DepartmentResponse> first = departmentService.findDepartments(true);
        assertThat(cacheManager.getCache(CacheConfig.DEPARTMENTS).get(true)).isNotNull();

        // A row inserted WITHOUT the service is not seen: the cached list is returned
        departmentRepository.save(new com.example.hospital.entity.Department("Hidden", "Block Z"));
        assertThat(departmentService.findDepartments(true)).isSameAs(first);

        // A change made THROUGH the service empties the cache, so the next read queries MySQL again
        createDepartment("Pediatrics");
        assertThat(cacheManager.getCache(CacheConfig.DEPARTMENTS).get(true)).isNull();
        assertThat(departmentService.findDepartments(true)).extracting(DepartmentResponse::name)
                .containsExactly("Cardiology", "Hidden", "Pediatrics");
    }

    @Test
    void doctorSummariesAreCached_andDeactivatingADoctorEvictsThem() {
        Long departmentId = createDepartment("Cardiology").id();
        Long d1 = createDoctor("D001", departmentId, "800.00").id();
        createDoctor("D002", departmentId, "700.00");

        assertThat(doctorService.findActiveDoctorSummaries()).hasSize(2);
        assertThat(cacheManager.getCache(CacheConfig.DOCTOR_SUMMARIES).get(org.springframework.cache.interceptor.SimpleKey.EMPTY))
                .isNotNull();

        doctorService.deactivateDoctor(d1);

        assertThat(doctorService.findActiveDoctorSummaries()).extracting(DoctorSummary::name)
                .containsExactly("Dr. D002");
    }
}
