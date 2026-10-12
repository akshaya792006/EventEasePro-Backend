package com.eventease.eventeasepro.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.eventease.eventeasepro.entity.Attendance;
import com.eventease.eventeasepro.entity.Attendance.AttendanceStatus;
import com.eventease.eventeasepro.service.AttendanceService;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:5173")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // Mark attendance using a valid QR token
    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(
            @RequestParam("eventId") Long eventId,
            @RequestParam("studentId") Long studentId,
            @RequestParam("token") String token) {

        try {
            Attendance attendance =
                    attendanceService.markAttendance(eventId, studentId, token);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Attendance marked successfully");
            response.put("attendanceId", attendance.getId());
            response.put("eventId", attendance.getEvent().getId());
            response.put("studentId", attendance.getStudent().getId());
            response.put("status", attendance.getStatus().name());
            response.put("attendanceTime", attendance.getAttendanceTime());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    // Get attendance records for an event
    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getAttendanceByEvent(
            @PathVariable("eventId") Long eventId) {

        try {
            List<Attendance> records =
                    attendanceService.getAttendanceByEvent(eventId);

            List<Map<String, Object>> response = records.stream()
                    .map(attendance -> {
                        Map<String, Object> item = new LinkedHashMap<>();

                        item.put("id", attendance.getId());
                        item.put("eventId", attendance.getEvent().getId());
                        item.put("eventTitle",
                                attendance.getEvent().getTitle());
                        item.put("studentId", attendance.getStudent().getId());
                        item.put("studentName",
                                attendance.getStudent().getFullName());
                        item.put("rollNumber",
                                attendance.getStudent().getRollNumber());
                        item.put("status", attendance.getStatus().name());
                        item.put("attendanceTime",
                                attendance.getAttendanceTime());

                        return item;
                    })
                    .toList();

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            ex.printStackTrace();

            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Unable to retrieve event attendance");
            error.put("details", ex.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }

    // Get attendance records for a student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getAttendanceByStudent(
            @PathVariable("studentId") Long studentId) {

        try {
            List<Attendance> records =
                    attendanceService.getAttendanceByStudent(studentId);

            List<Map<String, Object>> response = records.stream()
                    .map(attendance -> {
                        Map<String, Object> item = new LinkedHashMap<>();

                        item.put("id", attendance.getId());
                        item.put("eventId", attendance.getEvent().getId());
                        item.put("eventTitle",
                                attendance.getEvent().getTitle());
                        item.put("studentId", attendance.getStudent().getId());
                        item.put("studentName",
                                attendance.getStudent().getFullName());
                        item.put("status", attendance.getStatus().name());
                        item.put("attendanceTime",
                                attendance.getAttendanceTime());

                        return item;
                    })
                    .toList();

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            ex.printStackTrace();

            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Unable to retrieve student attendance");
            error.put("details", ex.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }

    // Update attendance status to PRESENT or ABSENT
    @PutMapping("/{attendanceId}/status")
    public ResponseEntity<?> updateAttendanceStatus(
            @PathVariable("attendanceId") Long attendanceId,
            @RequestBody Map<String, String> request) {

        try {
            String statusValue = request.get("status");

            if (statusValue == null || statusValue.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Status is required: PRESENT or ABSENT");
            }

            AttendanceStatus status;

            try {
                status = AttendanceStatus.valueOf(
                        statusValue.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid status. Use PRESENT or ABSENT.");
            }

            Attendance updated =
                    attendanceService.updateAttendanceStatus(
                            attendanceId, status);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Attendance status updated successfully");
            response.put("attendanceId", updated.getId());
            response.put("eventId", updated.getEvent().getId());
            response.put("studentId", updated.getStudent().getId());
            response.put("status", updated.getStatus().name());
            response.put("attendanceTime", updated.getAttendanceTime());

            return ResponseEntity.ok(response);

        } catch (ResponseStatusException ex) {
            throw ex;

        } catch (RuntimeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}