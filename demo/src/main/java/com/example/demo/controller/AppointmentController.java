package com.example.demo.controller;

import com.example.demo.model.Appointment;
import com.example.demo.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // 1. POST: Tạo Lịch Hẹn Mới (Yêu cầu xác thực, mọi Role đều được)
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody Appointment appointment) {
        // user_id sẽ được lấy từ token trong Service
        Appointment savedAppointment = appointmentService.createAppointment(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
    }

    // 2. GET: Xem Lịch Hẹn (Yêu cầu xác thực, lọc theo Role trong Service)
    // ADMIN/EMPLOYEE xem tất cả, CUSTOMER xem của mình
    @GetMapping
    public ResponseEntity<List<Appointment>> getAppointments() {
        // Truyền null vì ID người dùng sẽ được lấy trong Service
        List<Appointment> appointments = appointmentService.getAppointments(null);
        return ResponseEntity.ok(appointments);
    }

    // 3. PUT: Cập nhật Trạng thái (Chỉ ADMIN/EMPLOYEE)
    // URL: /api/appointments/1/status
    @PutMapping("/{id}/status")
    public ResponseEntity<Appointment> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        Appointment updatedAppointment = appointmentService.updateAppointmentStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedAppointment);
    }

    // DTO tạm thời cho request cập nhật trạng thái
    private static class StatusUpdateRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}