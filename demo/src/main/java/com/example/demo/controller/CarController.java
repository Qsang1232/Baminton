package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Car;
import com.example.demo.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarRepository carRepository;

    // 1. API CÔNG KHAI: Xem tất cả xe (GET)
    @GetMapping
    public ResponseEntity\u003cList\u003cCar\u003e\u003e getAllCars() {
        return ResponseEntity.ok(carRepository.findAll());
    }

    // 1.1. API CÔNG KHAI: Xem chi tiết xe theo ID (GET /{id})
    @GetMapping("/{id}")
    public ResponseEntity\u003cCar\u003e getCarById(@PathVariable Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -\u003e new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));
        return ResponseEntity.ok(car);
    }

    // 2. API BẢO VỆ: Thêm xe mới (POST) - Chỉ ADMIN \u0026 EMPLOYEE
    @PostMapping
    public ResponseEntity\u003cString\u003e addCar(@RequestBody Car car) {
        carRepository.save(car);
        return ResponseEntity.status(HttpStatus.CREATED).body("Xe " + car.getName() + " đã được thêm thành công.");
    }

    // 3. API BẢO VỆ: Sửa thông tin xe (PUT /{id}) - Chỉ ADMIN \u0026 EMPLOYEE
    @PutMapping("/{id}")
    public ResponseEntity\u003cString\u003e updateCar(@PathVariable Long id, @RequestBody Car carDetails) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -\u003e new ResourceNotFoundException("Không tìm thấy xe với ID: " + id)); // \u003c\u003c\u003c Sửa lỗi ở
                                                                                                      // đây

        // Cập nhật thông tin (Chỉ là ví dụ cơ bản)
        car.setName(carDetails.getName());
        car.setPrice(carDetails.getPrice());
        car.setDescription(carDetails.getDescription());
        carRepository.save(car);

        return ResponseEntity.ok("Xe với ID " + id + " đã được cập nhật.");
    }

    // 4. API BẢO VỆ: Xóa xe (DELETE /{id}) - Chỉ ADMIN \u0026 EMPLOYEE
    @DeleteMapping("/{id}")
    public ResponseEntity\u003cString\u003e deleteCar(@PathVariable Long id) {
        if (!carRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy xe với ID: " + id);
        }
        carRepository.deleteById(id);
        return ResponseEntity.ok("Xe với ID " + id + " đã được xóa thành công.");
    }

    // 5. API BẢO VỆ: Tải ảnh lên cho xe (POST /uploadImage/{id}) - Chỉ ADMIN \u0026 EMPLOYEE
    @PostMapping("/uploadImage/{id}")
    public ResponseEntity\u003cString\u003e uploadCarImage(@PathVariable Long id,
                                                @RequestParam("file") MultipartFile file) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -\u003e new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vui lòng chọn một tệp để tải lên.");
        }

        try {
            // Tạo tên tệp duy nhất
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path uploadPath = Paths.get("src/main/resources/static/uploads");
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // Cập nhật imageUrl cho xe
            car.setImageUrl("/uploads/" + fileName); // Lưu đường dẫn tương đối
            carRepository.save(car);

            return ResponseEntity.ok("Tải ảnh lên thành công. Đường dẫn: " + car.getImageUrl());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể tải tệp lên: " + e.getMessage());
        }
    }
}