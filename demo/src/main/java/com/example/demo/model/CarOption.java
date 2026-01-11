package com.example.demo.model;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Data
public class CarOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Ví dụ: Ghế Da Nappa, Bảng Cao Cấp, Màu Đỏ Pha Lê

    private BigDecimal price; // Chi phí của tùy chọn này

    private String description;

    // Mối quan hệ Many-to-Many: Một Option có thể thuộc về nhiều mẫu xe
    // MappedBy trỏ đến trường 'options' trong Car.java
    @ManyToMany(mappedBy = "options")
    private Set<Car> cars;
}