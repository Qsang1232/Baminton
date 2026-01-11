package com.example.demo.repository;

import com.example.demo.model.OwnedVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OwnedVehicleRepository extends JpaRepository\u003cOwnedVehicle, Long\u003e {
    // Tìm tất cả xe thuộc sở hữu của một khách hàng cụ thể
    List\u003cOwnedVehicle\u003e findByUserId(Long userId);
}