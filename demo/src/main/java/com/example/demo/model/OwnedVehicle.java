package com.example.demo.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "owned_vehicle")
@Data
public class OwnedVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Thông tin định danh xe ---

    // Số Khung (Ví dụ: VIN)
    @Column(unique = true, nullable = false)
    private String chassisNumber;

    // Số Máy
    private String engineNumber;

    // Biển số xe (ID BẢNG)
    private String licensePlate;

    private String color; // Màu xe

    // Ngày mua
    private LocalDate purchaseDate;

    // Số km đã đi
    private int mileage;
    // --- Mối quan hệ với Chủ sở hữu (Khách hàng) ---
    // ID CỦA KHÁCH HÀNG
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Đổi tên cột join
    private User user; // Đổi tên trường từ customer thành user

    // --- Mối quan hệ với Cấu hình xe (CarConfiguration) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_configuration_id") // Đổi tên cột join
    private CarConfiguration carConfiguration; // Đổi tên trường từ carModel thành carConfiguration


    // --- Thông tin Bảo dưỡng/Bảo hành gần nhất ---

    // Ngày Tháng Đã Bảo Hành
    private LocalDate lastWarrantyDate;

    // Ngày Tháng Đã Bảo Dưỡng
    private LocalDate lastMaintenanceDate;

    // Bạn có thể mở rộng bằng cách thêm một Collection\u003cMaintenanceHistory\u003e nếu cần
    // lịch sử chi tiết
}