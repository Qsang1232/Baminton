package com.example.demo.repository;

import com.example.demo.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Thêm phương thức này để Khách hàng có thể xem lịch sử đặt hẹn của mình
    List<Appointment> findByUserId(Long userId);
}