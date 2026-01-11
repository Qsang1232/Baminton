package com.example.demo.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@Data
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Mối quan hệ ---

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Khách hàng đặt lịch

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owned_vehicle_id")
    private OwnedVehicle ownedVehicle; // Xe thuộc sở hữu của khách hàng

    // --- Thông tin Lịch hẹn ---

    private LocalDateTime appointmentDate; // Thời gian đặt lịch

    @Enumerated(EnumType.STRING)
    private AppointmentType appointmentType; // Loại dịch vụ (MAINTENANCE, REPAIR, etc.)

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.PENDING; // Trạng thái lịch hẹn

    private String notes; // Ghi chú của khách hàng (Được đặt ở cuối)
}