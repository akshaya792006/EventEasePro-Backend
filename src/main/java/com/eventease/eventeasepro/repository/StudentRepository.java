package com.eventease.eventeasepro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventease.eventeasepro.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    Optional<Student> findByRollNumber(String rollNumber);

    boolean existsByEmail(String email);

    boolean existsByRollNumber(String rollNumber);
}