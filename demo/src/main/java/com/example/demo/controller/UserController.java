package com.example.demo.controller;

import com.example.demo.dto.UserCreationRequest;
import com.example.demo.model.User;
import com.example.demo.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authService;

    // POST /api/admin/users (Chỉ ADMIN được phép)
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserCreationRequest request) {

        // Tạo đối tượng User
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(request.getPassword());

        // Đảm bảo vai trò được truyền vào là ADMIN hoặc EMPLOYEE
        if (request.getRole() == null) {
            return ResponseEntity.badRequest().body("Vai trò (Role) không được để trống.");
        }
        newUser.setRole(request.getRole());

        // Sử dụng logic đăng ký từ AuthenticationService
        authService.register(newUser);

        return ResponseEntity.ok("Tài khoản " + request.getUsername() + " với vai trò " + request.getRole().name()
                + " đã được tạo thành công.");
    }
}