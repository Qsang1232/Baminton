package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    // Thư mục lưu ảnh (sẽ tạo thư mục 'uploads' ngay tại root dự án)
    private static final String UPLOAD_DIR = "uploads";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Tạo thư mục nếu chưa có
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Sinh tên file mới để tránh trùng (VD: asd12-image.jpg)
            String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            
            // 3. Lưu file vào máy
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Trả về đường dẫn URL để lưu vào DB
            // Frontend sẽ ghép: http://localhost:8080/uploads/ten-file.jpg
            String fileUrl = "/uploads/" + fileName;

            return ResponseEntity.ok(Map.of("url", fileUrl));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Lỗi upload: " + e.getMessage());
        }
    }
}