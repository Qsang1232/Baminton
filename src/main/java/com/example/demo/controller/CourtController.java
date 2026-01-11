package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Court;
import com.example.demo.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    // --- SỬA PHẦN NÀY ĐỂ HIỆN ẢNH TỪ 1 ĐẾN 25 ---
    @GetMapping
    public ResponseEntity<ApiResponse<List<Court>>> getAllCourts() {
        List<Court> courts = courtService.getAllCourts();

        // CẤU HÌNH ẢNH (Bắt đầu thêm code ở đây)
        String baseUrl = "http://localhost:8080/images/";

        for (int i = 0; i < courts.size(); i++) {
            // Logic chia dư để quay vòng ảnh từ 1 đến 25
            // i=0 -> san1, ..., i=24 -> san25, i=25 -> quay lại san1
            int imgIndex = (i % 25) + 1; 
            String imgUrl = baseUrl + "san" + imgIndex + ".jpg";

            // Gán ảnh vào đối tượng Court
            // LƯU Ý: Kiểm tra file Court.java xem tên hàm set là setImage hay setImageUrl nhé!
            courts.get(i).setImageUrl(imgUrl); 
        }
        // (Kết thúc code thêm)

        ApiResponse<List<Court>> response = ApiResponse.<List<Court>>builder()
                .success(true)
                .message("Lấy danh sách sân thành công")
                .data(courts)
                .build();

        return ResponseEntity.ok(response);
    }

    // --- SỬA THÊM CÁI NÀY ĐỂ KHI BẤM VÀO CHI TIẾT CŨNG CÓ ẢNH ---
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Court>> getCourtById(@PathVariable Long id) {
        Court court = courtService.getCourtById(id);

        // Code gán ảnh cho chi tiết (hack tạm theo ID)
        String baseUrl = "http://localhost:8080/images/";
        // Lấy ID chia dư cho 25 để ra số thứ tự ảnh
        long imgIndex = (court.getId() % 25);
        if (imgIndex == 0) imgIndex = 25; // Nếu chia hết thì lấy ảnh 25
        
      court.setImageUrl(baseUrl + "san" + imgIndex + ".jpg");

        ApiResponse<Court> response = ApiResponse.<Court>builder()
                .success(true)
                .message("Tìm thấy sân thành công")
                .data(court)
                .build();

        return ResponseEntity.ok(response);
    }

    // --- CÁC HÀM DƯỚI GIỮ NGUYÊN ---

    // 3. Tạo mới sân (Dành cho Admin)
    @PostMapping
    public ResponseEntity<ApiResponse<Court>> createCourt(@RequestBody Court court) {
        Court newCourt = courtService.createCourt(court);
        ApiResponse<Court> response = ApiResponse.<Court>builder()
                .success(true)
                .message("Tạo sân mới thành công")
                .data(newCourt)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 4. Cập nhật sân (Dành cho Admin)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Court>> updateCourt(@PathVariable Long id, @RequestBody Court courtDetails) {
        Court updatedCourt = courtService.updateCourt(id, courtDetails);
        ApiResponse<Court> response = ApiResponse.<Court>builder()
                .success(true)
                .message("Cập nhật thông tin sân thành công")
                .data(updatedCourt)
                .build();
        return ResponseEntity.ok(response);
    }

    // 5. Xóa sân (Dành cho Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa sân thành công")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}