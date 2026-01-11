package com.example.demo.controller;

import com.example.demo.dto.ConfigurationRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.CarConfigurationRepository;
import com.example.demo.repository.CarOptionRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class CarConfigurationController {

    private final CarConfigurationRepository configRepository;
    private final CarRepository carRepository;
    private final CarOptionRepository optionRepository;
    private final UserRepository userRepository;

    // Lấy User hiện tại (từ logic đã xây dựng)
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -\u003e new RuntimeException("Người dùng không tìm thấy."));
    }

    // DTO cho Request (Front-end sẽ gửi lên ID mẫu xe và danh sách ID option)
    private static class ConfigurationRequest {
        private Long carModelId;
        private Set\u003cLong\u003e optionIds;
        private String selectedColor;

        // Getter/Setter (cần Lombok @Data nếu không dùng inner class)
        public Long getCarModelId() {
            return carModelId;
        }

        public Set\u003cLong\u003e getOptionIds() {
            return optionIds;
        }

        public String getSelectedColor() {
            return selectedColor;
        }

        public void setCarModelId(Long id) {
            this.carModelId = id;
        }

        public void setOptionIds(Set\u003cLong\u003e ids) {
            this.optionIds = ids;
        }

        public void setSelectedColor(String color) {
            this.selectedColor = color;
        }
    }

    // POST: Thêm cấu hình vào "Giỏ hàng" (Tạo/Cập nhật Config)
    /**
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity\u003cCarConfiguration\u003e saveConfiguration(@RequestBody ConfigurationRequest request) {
        User customer = getCurrentUser();
        Car baseCar = carRepository.findById(request.getCarModelId())
                .orElseThrow(() -\u003e new ResourceNotFoundException("Không tìm thấy mẫu xe cơ bản."));

        // Lấy tất cả options đã chọn
        List\u003cCarOption\u003e selectedOptions = optionRepository.findAllById(request.getOptionIds());

        // Tính tổng giá
        BigDecimal optionsPrice = selectedOptions.stream()
                .map(CarOption::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPrice = baseCar.getPrice().add(optionsPrice);

        // Tạo cấu hình mới
        CarConfiguration config = new CarConfiguration();
        config.setCustomer(customer);
        config.setBaseCarModel(baseCar);
        config.setSelectedOptions(new HashSet\u003c\u003e(selectedOptions));
        config.setSelectedColor(request.getSelectedColor());
        config.setTotalPrice(totalPrice);
        config.setStatus(ConfigStatus.PENDING_APPOINTMENT); // Chờ đặt lịch

        CarConfiguration savedConfig = configRepository.save(config);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedConfig);
    }

    // GET: Xem "Giỏ hàng" cấu hình hiện tại
    @GetMapping("/active")
    public ResponseEntity\u003cList\u003cCarConfiguration\u003e\u003e getActiveConfiguration() {
        User customer = getCurrentUser();

        // Ensure all parameters are separated by a comma (,) inside the parenthesis
        List\u003cCarConfiguration\u003e configs = configRepository.findByCustomerAndStatus(
                customer,
                ConfigStatus.PENDING_APPOINTMENT);

        return ResponseEntity.ok(configs);
    }
}