package com.example.demo.service;

import com.example.demo.model.Appointment;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    // Phương thức tiện ích để lấy User đang đăng nhập từ Security Context
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tìm thấy."));
    }

    // 1. Logic Tạo Lịch Hẹn (POST)
    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        User user = getCurrentUser();

        // Gán người dùng hiện tại cho lịch hẹn
        appointment.setUser(user);

        // Đảm bảo không đặt lịch trong quá khứ
        if (appointment.getAppointmentDate() == null) {
            throw new IllegalArgumentException("Thời gian đặt lịch không hợp lệ.");
        }

        return appointmentRepository.save(appointment);
    }

    // 2. Logic Xem Lịch Hẹn (GET)
    public List<Appointment> getAppointments(Long userId) {
        User user = getCurrentUser();

        // Lấy vai trò của người dùng
        Role role = user.getRole();

        if (role == Role.ADMIN || role == Role.EMPLOYEE) {
            // ADMIN và EMPLOYEE được xem tất cả
            return appointmentRepository.findAll();
        } else if (role == Role.CUSTOMER) {
            // CUSTOMER chỉ xem được lịch hẹn của chính mình (sử dụng ID trong token)
            return appointmentRepository.findByUserId(user.getId());
        }

        // Trường hợp còn lại (lỗi hoặc vai trò không xác định)
        throw new IllegalStateException("Quyền truy cập không hợp lệ.");
    }

    // 3. Logic Cập nhật Trạng thái (PUT)
    @Transactional
    public Appointment updateAppointmentStatus(Long id, String status) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.EMPLOYEE) {
            throw new SecurityException("Chỉ ADMIN và EMPLOYEE mới được cập nhật trạng thái.");
        }

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn với ID: " + id));

        // Cập nhật trạng thái
        try {
            appointment.setStatus(com.example.demo.model.AppointmentStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }

        return appointmentRepository.save(appointment);
    }
}