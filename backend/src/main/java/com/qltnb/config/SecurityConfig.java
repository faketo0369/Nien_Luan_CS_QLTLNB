package com.qltnb.config;

import com.qltnb.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 0. Mở endpoint /error để Spring Boot forward lỗi không bị chặn bởi Security
                .requestMatchers("/error").permitAll()

                // 1. Cổng đăng nhập mở tự do công khai
                .requestMatchers("/api/auth/**").permitAll()

                // 2. Phân quyền chi tiết cho module Tài liệu & Văn bản hành chính
                .requestMatchers(HttpMethod.GET, "/api/documents", "/api/documents/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/documents/**").hasAnyAuthority("NHAN_VIEN", "TRUONG_PHONG", "ADMIN")
                .requestMatchers("/api/documents/*/approval/**").hasAnyAuthority("TRUONG_PHONG", "ADMIN")
                .requestMatchers("/api/documents/*/permissions/**").hasAnyAuthority("TRUONG_PHONG", "ADMIN")

                // 3. Nghiệp vụ đặc thù công ty luật (Khách hàng & Vụ việc) - Cần có tài khoản là dùng được
                .requestMatchers("/api/clients/**").authenticated()
                .requestMatchers("/api/cases/**").authenticated()
                .requestMatchers("/api/search/**").authenticated()
                .requestMatchers("/api/notifications/**").authenticated()

                // 4. Hộp đen kiểm toán & Nhật ký - Chỉ cấp quản lý được xem
                .requestMatchers("/api/activity-logs/**").hasAnyAuthority("TRUONG_PHONG", "ADMIN")

                // 5. Quản trị danh mục nền hệ thống - Phân quyền quản lý tài liệu và người dùng
                .requestMatchers("/api/users", "/api/users/**").hasAnyAuthority("ADMIN", "TRUONG_PHONG")
                .requestMatchers("/api/departments/**").hasAuthority("ADMIN")
                .requestMatchers("/api/categories/**").hasAuthority("ADMIN")

                // Tất cả các request phát sinh còn lại đều bắt buộc phải đăng nhập
                .anyRequest().authenticated()
            );

        // Chèn bộ lọc kiểm tra JWT Token trước khi tiến hành xử lý yêu cầu dữ liệu
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
