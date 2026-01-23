package com.example.demo.config;

import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Xử lý lỗi Validation (@Valid) -> Trả về 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // --- [MỚI THÊM] ---
    // 2. Xử lý ResponseStatusException (Lỗi logic mình tự ném ra ở Service)
    // Giúp trả về đúng status (400) thay vì bị ép về 500
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> response = new HashMap<>();
        // ex.getReason() chỉ lấy nội dung tiếng Việt (ví dụ: "Sân chưa mở cửa"), bỏ qua mấy chữ kỹ thuật
        response.put("message", ex.getReason());
        
        // Trả về đúng status code mà mình đã ném ra (ví dụ BAD_REQUEST 400)
        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    // 3. Xử lý lỗi ResourceNotFoundException -> Trả về JSON { message: "..." }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 4. Xử lý lỗi Runtime chung (Fallback) -> Trả về 500
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        // In ra console server để dev sửa lỗi
        ex.printStackTrace();
        response.put("message", "Lỗi hệ thống: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // 5. Bắt tất cả các lỗi còn lại (Exception)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        ex.printStackTrace();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã xảy ra lỗi không xác định.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}