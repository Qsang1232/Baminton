package com.example.demo.model;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String manufacturer;
    private Integer year;
    private BigDecimal price;
    private String description;
    private String imageUrl;

    // >>> THÊM MỐI QUAN HỆ VỚI CATEGORY <<<
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) // Tải Category khi cần
    @JoinColumn(name = "category_id")
    private Category category;

    // Lưu ý: Thêm trường này vào DTO khi POST/PUT Car
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "car_has_options", joinColumns = @JoinColumn(name = "car_id"), inverseJoinColumns = @JoinColumn(name = "option_id"))
    private Set<CarOption> options;
}