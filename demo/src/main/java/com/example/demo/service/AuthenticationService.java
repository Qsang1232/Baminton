package com.example.demo.service;

import com.example.demo.dto.AuthRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String register(User user) {
        // 1. Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // 2. Tạo Token và trả về
        return jwtService.generateToken(user);
    }

    public String authenticate(String loginIdentifier, String password) {
        // 1. Xác thực Credentials bằng AuthenticationManager
        // Nếu mật khẩu sai, BadCredentialsException sẽ được ném ra ngay tại đây.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginIdentifier, password));

        // 2. Nếu xác thực thành công, tìm UserDetails và tạo Token
        Optional\u003cUser\u003e userOptional = userRepository.findByUsername(loginIdentifier);

        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(loginIdentifier);
        }

        User user = userOptional.orElseThrow(() -\u003e new RuntimeException("User not found"));

        // 3. LOG CHỈ KHI TẤT CẢ ĐÃ THÀNH CÔNG
        log.info("Người dùng {} đã đăng nhập thành công.", user.getUsername());

        return jwtService.generateToken(user);
    }
}