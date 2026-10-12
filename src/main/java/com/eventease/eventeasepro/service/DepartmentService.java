package com.eventease.eventeasepro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventease.eventeasepro.entity.Department;
import com.eventease.eventeasepro.repository.DepartmentRepository;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department createDepartment(Department department) {
        department.setId(null);

        if (departmentRepository.existsByDepartmentNameIgnoreCase(
                department.getDepartmentName())) {
            throw new IllegalArgumentException(
                    "Department already exists");
        }

        return departmentRepository.save(department);
    }

    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Department not found with ID: " + id));
    }

    public Department updateDepartment(Long id, Department updatedDepartment) {
        Department existing = getDepartmentById(id);

        departmentRepository
                .findByDepartmentNameIgnoreCase(
                        updatedDepartment.getDepartmentName())
                .ifPresent(department -> {
                    if (!department.getId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Department name already exists");
                    }
                });

        existing.setDepartmentName(updatedDepartment.getDepartmentName());
        existing.setCoordinatorName(updatedDepartment.getCoordinatorName());
        existing.setCoordinatorEmail(updatedDepartment.getCoordinatorEmail());
        existing.setContactNumber(updatedDepartment.getContactNumber());

        return departmentRepository.save(existing);
    }

    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }
}