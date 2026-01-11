package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -\u003e csrf.disable())
                .authorizeHttpRequests(auth -\u003e auth
                        // API Đăng ký/Đăng nhập (Mở cho tất cả)
                        .requestMatchers("/api/auth/**").permitAll()

                        // API Xem Xe (Mở cho tất cả mọi người, kể cả chưa đăng nhập)
                        .requestMatchers(HttpMethod.GET, "/api/cars/**").permitAll()

                        // API QUẢN LÝ XE: Chỉ ADMIN và EMPLOYEE được phép Thêm, Sửa, Xóa
                        .requestMatchers(HttpMethod.POST, "/api/cars/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.PUT, "/api/cars/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.DELETE, "/api/cars/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll() // Xem công khai
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasAnyRole("ADMIN", "EMPLOYEE") // Quản
                        .requestMatchers("/api/admin/users/**").hasRole("ADMIN") // lý
                        // danh
                        // mục
                        .requestMatchers(HttpMethod.GET, "/api/car-options/**").permitAll() // Xem công khai
                        .requestMatchers(HttpMethod.POST, "/api/car-options/**").hasAnyRole("ADMIN", "EMPLOYEE") // Quản
                        .requestMatchers("/api/config/**").authenticated() // lý
                        // tùy
                        // chọn
                        .requestMatchers(HttpMethod.PUT, "/api/car-options/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.DELETE, "/api/car-options/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        // API ĐẶT LỊCH:
                        // - Khách hàng, Nhân viên, Admin đều có thể tạo lịch hẹn
                        // - Nhân viên/Admin có thể xem và quản lý tất cả lịch hẹn
                        .requestMatchers(HttpMethod.POST, "/api/appointments")
                        .hasAnyRole("CUSTOMER", "EMPLOYEE", "ADMIN") // Tạo
                        // lịch
                        // hẹn
                        .requestMatchers("/api/appointments/**").hasAnyRole("ADMIN", "EMPLOYEE") // Quản lý/Xem lịch hẹn

                        // API Test (Vẫn để yêu cầu bất kỳ ai đã xác thực)
                        .requestMatchers("/api/test/**").authenticated()

                        // Các request khác (nếu có) phải được xác thực
                        .anyRequest().permitAll())
                .sessionManagement(sess -\u003e sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}