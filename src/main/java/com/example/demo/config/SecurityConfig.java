package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
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
            .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 1. CẤU HÌNH CORS TẠI ĐÂY
            
            .authorizeHttpRequests(auth -> auth
                // --- PUBLIC ENDPOINTS ---
                .requestMatchers("/images/**").permitAll() 
                .requestMatchers("/api/auth/**").permitAll()     
                .requestMatchers("/api/payment/**").permitAll()  
                .requestMatchers(HttpMethod.GET, "/api/courts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                // --- ADMIN ENDPOINTS ---
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/bookings/all").hasRole("ADMIN")
                .requestMatchers("/api/bookings/court/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/courts/**", "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/courts/**", "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/courts/**", "/api/categories/**").hasRole("ADMIN")

                // --- AUTHENTICATED ENDPOINTS ---
                .requestMatchers("/api/bookings/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/reviews").authenticated()
                .requestMatchers("/api/users/**").authenticated()

                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 2. BEAN CẤU HÌNH CORS CHI TIẾT
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        

            configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
        // Cho phép tất cả các method quan trọng
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        
        // Cho phép mọi header (bao gồm Authorization để gửi Token)
        configuration.setAllowedHeaders(List.of("*"));
        
        // Cho phép gửi cookie/credentials (quan trọng nếu dùng session hoặc token phức tạp)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Áp dụng cấu hình này cho tất cả đường dẫn API
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}