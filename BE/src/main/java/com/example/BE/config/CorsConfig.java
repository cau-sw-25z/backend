package com.example.BE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// 프론트엔드 로컬 개발 서버에서 백엔드 API를 호출할 수 있도록 CORS 설정
@Configuration
public class CorsConfig {

    // Spring Security에서 사용할 CORS 설정 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 로컬 개발 서버 주소 허용
        // Vite 기본 포트는 5173
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // 허용할 HTTP 메서드 목록
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 모든 요청 헤더 허용
        configuration.setAllowedHeaders(List.of("*"));

        // 쿠키, 인증 정보 등을 포함한 요청 허용
        configuration.setAllowCredentials(true);

        // 위 CORS 설정을 모든 API 경로에 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}