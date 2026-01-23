package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth

            // 1. OPTIONS cho CORS (Luôn cho phép đầu tiên)
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // 2. TÀI NGUYÊN CÔNG KHAI HOÀN TOÀN (Ảnh, Login, Swagger, Payment Callback)
            .requestMatchers(
                    "/api/auth/**",
                    "/api/payment/**",  // Để Momo/VNPay gọi lại không cần login
                    "/images/**", 
                     "/uploads/**",      // <--- THÊM: Cho phép xem ảnh upload
                        "/api/upload",       // Quan trọng: Để load ảnh sân
                    "/v3/api-docs/**",
                    "/swagger-ui/**"
            ).permitAll()

            // 3. CHO PHÉP XEM DỮ LIỆU (Chỉ Method GET) -> Khách vãng lai xem được
            .requestMatchers(HttpMethod.GET, 
                    "/api/courts/**", 
                    "/api/categories/**", 
                    "/api/reviews/**"
            ).permitAll()

            // 4. QUYỀN ADMIN (Thứ tự quan trọng)
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/bookings/all").hasRole("ADMIN")
            .requestMatchers("/api/bookings/*/confirm").hasRole("ADMIN")
            
            // Chỉ Admin mới được Thêm/Sửa/Xóa Sân & Danh mục
            .requestMatchers(HttpMethod.POST, "/api/courts/**", "/api/categories/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/courts/**", "/api/categories/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/courts/**", "/api/categories/**").hasRole("ADMIN")

            // 5. CÁC QUYỀN KHÁC (USER & ADMIN)
            .requestMatchers("/api/bookings/*/cancel").hasAnyRole("ADMIN", "USER")
            
            // Booking và Review bắt buộc phải đăng nhập
            .requestMatchers("/api/bookings/**").authenticated()
            .requestMatchers("/api/reviews/**").authenticated() // POST review cần login
            .requestMatchers("/api/users/**").authenticated()

            // 6. CÒN LẠI KHÓA HẾT
            .anyRequest().authenticated()
        )
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}

    // ===== CORS =====
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:3000",
            "https://badminton-sals.vercel.app" 
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
