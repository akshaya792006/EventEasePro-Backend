package com.eventease.eventeasepro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventease.eventeasepro.entity.Attendance;
import com.eventease.eventeasepro.entity.Attendance.AttendanceStatus;

@Repository
public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    List<Attendance> findByEventId(Long eventId);

    List<Attendance> findByStudentId(Long studentId);

    Optional<Attendance> findByEventIdAndStudentId(
            Long eventId, Long studentId);

    List<Attendance> findByEventIdAndStatus(
            Long eventId, AttendanceStatus status);

    boolean existsByEventIdAndStudentId(
            Long eventId, Long studentId);
}