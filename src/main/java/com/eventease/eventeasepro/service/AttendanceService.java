package com.eventease.eventeasepro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventease.eventeasepro.entity.Attendance;
import com.eventease.eventeasepro.entity.Attendance.AttendanceStatus;
import com.eventease.eventeasepro.entity.Event;
import com.eventease.eventeasepro.entity.Student;
import com.eventease.eventeasepro.repository.AttendanceRepository;
import com.eventease.eventeasepro.repository.EventRepository;
import com.eventease.eventeasepro.repository.StudentRepository;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EventRepository eventRepository;
    private final StudentRepository studentRepository;
    private final QrTokenService qrTokenService;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            EventRepository eventRepository,
            StudentRepository studentRepository,
            QrTokenService qrTokenService) {

        this.attendanceRepository = attendanceRepository;
        this.eventRepository = eventRepository;
        this.studentRepository = studentRepository;
        this.qrTokenService = qrTokenService;
    }

    // Mark attendance after validating the QR token
    @Transactional
    public Attendance markAttendance(
            Long eventId,
            Long studentId,
            String token) {

        boolean valid = qrTokenService.validateToken(eventId, token);

        if (!valid) {
            throw new RuntimeException(
                    "Invalid or expired QR token");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        boolean alreadyMarked =
                attendanceRepository.existsByEventIdAndStudentId(
                        eventId, studentId);

        if (alreadyMarked) {
            throw new RuntimeException(
                    "Attendance already marked for this event");
        }

        Attendance attendance = new Attendance();

        attendance.setEvent(event);
        attendance.setStudent(student);
        attendance.setStatus(AttendanceStatus.PRESENT);

        return attendanceRepository.save(attendance);
    }

    // Get attendance records for an event
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByEvent(Long eventId) {

        return attendanceRepository.findByEventId(eventId);
    }

    // Get attendance records for a student
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByStudent(Long studentId) {

        return attendanceRepository.findByStudentId(studentId);
    }

    // Update attendance status to PRESENT or ABSENT
    @Transactional
    public Attendance updateAttendanceStatus(
            Long attendanceId,
            AttendanceStatus status) {

        Attendance attendance = attendanceRepository
                .findById(attendanceId)
                .orElseThrow(() ->
                        new RuntimeException("Attendance not found"));

        attendance.setStatus(status);

        return attendanceRepository.save(attendance);
    }
}