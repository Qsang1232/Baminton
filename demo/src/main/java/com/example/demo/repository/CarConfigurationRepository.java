package com.example.demo.repository;

import com.example.demo.model.CarConfiguration;
import com.example.demo.model.ConfigStatus; // Cần import ConfigStatus
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarConfigurationRepository extends JpaRepository\u003cCarConfiguration, Long\u003e {
    // Đảm bảo tên phương thức và thứ tự tham số khớp với tên trường trong Entity:
    // Tên trường trong Entity CarConfiguration: customer (là User) và status (là
    // ConfigStatus)
    // Trong CarConfigurationRepository.java

    List\u003cCarConfiguration\u003e findByCustomerAndStatus(User customer, ConfigStatus status);
    // HOẶC nếu bạn muốn dùng ID, tên phương thức phải là:
    // List\u003cCarConfiguration\u003e findByCustomerIdAndStatus(Long customerId,
    // ConfigStatus status);
}