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
            .csrf(AbstractHttpConfigurer::disable)
            // Kích hoạt CORS với config bên dưới
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .authorizeHttpRequests(auth -> auth
                // QUAN TRỌNG: Cho phép method OPTIONS đi qua mà không cần token
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                
                // PUBLIC
                .requestMatchers("/images/**", "/api/auth/**", "/api/payment/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/courts/**", "/api/categories/**", "/api/reviews/**", "/api/bookings/check-availability").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                // ADMIN & USER routes (Giữ nguyên code của bạn)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/bookings/all").hasRole("ADMIN")
                .requestMatchers("/api/bookings/*/confirm").hasRole("ADMIN")
                .requestMatchers("/api/bookings/*/cancel").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/courts/**", "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/courts/**", "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/courts/**", "/api/categories/**").hasRole("ADMIN")

                .requestMatchers("/api/bookings/**").authenticated()
                .requestMatchers("/api/reviews").authenticated()
                .requestMatchers("/api/users/**").authenticated()

                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Cho phép các domain này
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "https://badminton-sals.vercel.app"
        ));
        // Cho phép đầy đủ các method
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        // Cho phép mọi header
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "x-auth-token"));
        // Cho phép gửi credentials (cookie/token)
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}