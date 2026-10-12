package com.eventease.eventeasepro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventease.eventeasepro.entity.Department;

public interface DepartmentRepository
        extends JpaRepository<Department, Long> {

    Optional<Department> findByDepartmentNameIgnoreCase(
            String departmentName);

    boolean existsByDepartmentNameIgnoreCase(String departmentName);
}