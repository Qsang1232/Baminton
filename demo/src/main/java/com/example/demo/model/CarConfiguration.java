package com.example.demo.model; // Sử dụng package của bạn

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "car_configuration")
@Data
public class CarConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Liên kết với Chủ sở hữu ---
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // --- Liên kết với Mẫu xe cơ bản ---
    @ManyToOne
    @JoinColumn(name = "car_model_id", nullable = false)
    private Car baseCarModel;

    // --- Thông tin cấu hình chi tiết ---
    private String selectedColor;
    private String interiorMaterial;
    private String wheelType;
    private BigDecimal totalPrice;

    // --- Các tùy chọn đã chọn (Liên kết N-N với Option) ---
    @ManyToMany
    @JoinTable(name = "config_selected_options", joinColumns = @JoinColumn(name = "config_id"), inverseJoinColumns = @JoinColumn(name = "option_id"))
    private Set\u003cCarOption\u003e selectedOptions;

    // --- Trạng thái ---
    @Enumerated(EnumType.STRING)
    private ConfigStatus status; // Sử dụng enum đã tách ra file riêng

    public CarConfiguration() {
        this.selectedOptions = new HashSet\u003c\u003e();
    }

    public void addOption(CarOption option) {
        this.selectedOptions.add(option);
    }
}