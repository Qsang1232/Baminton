package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BookingRequest;
import com.example.demo.model.Booking;
import com.example.demo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 1. Tạo đơn (User)
    @PostMapping
    public ResponseEntity<ApiResponse<Booking>> createBooking(@RequestBody BookingRequest request) {
        Booking newBooking = bookingService.createBooking(
                request.getCourtId(),
                request.getStartTime(),
                request.getEndTime()
        );
        return new ResponseEntity<>(ApiResponse.<Booking>builder()
                .success(true)
                .message("Đặt sân thành công! Vui lòng thanh toán.")
                .data(newBooking)
                .build(), HttpStatus.CREATED);
    }

    // 2. Lịch sử của tôi (User)
    @GetMapping("/my-history")
    public ResponseEntity<ApiResponse<List<Booking>>> getMyHistory() {
        return ResponseEntity.ok(ApiResponse.<List<Booking>>builder()
                .success(true)
                .message("Lịch sử đặt sân của bạn")
                .data(bookingService.getMyBookings())
                .build());
    }

    // 3. Hủy đơn (User/Admin)
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Hủy lịch thành công")
                .build());
    }

    // 4. Tất cả đơn (Admin)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Booking>>> getAllBookings() {
        return ResponseEntity.ok(ApiResponse.<List<Booking>>builder()
                .success(true)
                .message("Toàn bộ lịch sử hệ thống")
                .data(bookingService.getAllBookings())
                .build());
    }

    // 5. Đơn theo sân (Admin)
    // Sửa lỗi: Gọi đúng hàm getBookingsByCourt
    @GetMapping("/court/{courtId}")
    public ResponseEntity<ApiResponse<List<Booking>>> getBookingsByCourt(@PathVariable Long courtId) {
        // Nếu trong BookingService chưa có hàm này, bạn phải thêm vào (xem bên dưới)
        // Hoặc tạm thời dùng hàm lọc thủ công
        List<Booking> bookings = bookingService.getAllBookings().stream()
                .filter(b -> b.getCourt().getId().equals(courtId))
                .toList();

        return ResponseEntity.ok(ApiResponse.<List<Booking>>builder()
                .success(true)
                .message("Lịch đặt của sân ID: " + courtId)
                .data(bookings)
                .build());
    }
}