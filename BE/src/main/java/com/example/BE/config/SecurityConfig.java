package com.example.BE.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Spring Security 관련 설정 클래스
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    // Spring Security의 필터 체인을 Bean으로 등록
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API 서버에서는 CSRF 보호 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // Form Login / Basic Auth 사용 안 함
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // CorsConfig에서 정의한 CORS 설정 적용
                .cors(Customizer.withDefaults())

                // JWT 기반 인증이므로 세션 사용 안 함
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 요청별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // CORS preflight 요청 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Health Check API
                        .requestMatchers("/api/health").permitAll()

                        // Auth API
                        .requestMatchers("/api/auth/**").permitAll()

                        // Swagger UI
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                                       
                        // 설문 문항 조회 API 접근 허용
                        .requestMatchers("/survey/**").permitAll()

                        // 그 외 API는 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터 등록
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // 비밀번호 암호화용 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}