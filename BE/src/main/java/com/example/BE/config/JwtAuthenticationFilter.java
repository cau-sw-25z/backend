package com.example.BE.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Header에서 토큰 꺼내기
        String token = extractToken(request);

        // 2. 토큰 유효하면 SecurityContext에 저장
        if (token != null && jwtUtil.validateToken(token)) {
            Long userId = jwtUtil.getUserId(token);
            String email = jwtUtil.getEmail(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, List.of());
            authentication.setDetails(userId);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 다음 필터로 넘기기
        filterChain.doFilter(request, response);
    }

    // Header에서 토큰 추출
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}