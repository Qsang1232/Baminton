package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final BookingService bookingService;

    // 1. API TẠO URL THANH TOÁN (Frontend gọi cái này)
    @GetMapping("/create-payment-url")
    public ResponseEntity<ApiResponse<String>> createPaymentUrl(@RequestParam Long bookingId) {
        // Trong thực tế: Code này sẽ gọi thư viện VNPay để tạo ra URL dài ngoằng chứa mã hóa.
        
        // Trong Demo: Chúng ta trả về chính cái URL callback của server mình
        // Kèm theo mã thành công (00)
        String mockUrl = "http://localhost:8080/api/payment/vnpay-return?vnp_TxnRef=" + bookingId + "&vnp_ResponseCode=00";

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Tạo URL thanh toán thành công")
                .data(mockUrl) 
                .build());
    }

    // 2. API XỬ LÝ KẾT QUẢ (Callback - Đã làm trước đó)
    @GetMapping("/vnpay-return")
    public ResponseEntity<ApiResponse<String>> vnpayReturn(
            @RequestParam("vnp_TxnRef") String bookingIdStr,
            @RequestParam("vnp_ResponseCode") String responseCode
    ) {
        log.info("VNPay callback: bookingId={}, responseCode={}", bookingIdStr, responseCode);

        if ("00".equals(responseCode)) {
            Long bookingId = Long.parseLong(bookingIdStr);
            bookingService.confirmBookingPayment(bookingId);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Thanh toán thành công! Đơn hàng đã được xác nhận.")
                    .data("CONFIRMED")
                    .build());
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Thanh toán thất bại.")
                    .data("FAILED")
                    .build());
        }
    }
}