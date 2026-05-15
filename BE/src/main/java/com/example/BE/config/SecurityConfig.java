package com.example.BE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Spring Security 관련 설정 클래스
@Configuration
public class SecurityConfig {

    // Spring Security의 필터 체인을 Bean으로 등록
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API 서버에서는 일반적으로 CSRF 보호를 비활성화함
                // JWT 기반 인증을 사용할 경우에도 보통 disable 처리함
                .csrf(csrf -> csrf.disable())

                // CorsConfig에서 정의한 CORS 설정을 Spring Security에 적용
                .cors(Customizer.withDefaults())

                // 요청별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // Health Check API는 인증 없이 접근 가능
                        .requestMatchers("/api/health").permitAll()

                        // Swagger UI 접근 허용
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()

                        // Swagger API 문서 JSON 접근 허용
                        .requestMatchers("/v3/api-docs/**").permitAll()

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );

        // 설정이 적용된 SecurityFilterChain 반환
        return http.build();
    }
}