package com.example.BE.dto;

// Health Check API의 응답 데이터를 담는 DTO
// Java 17의 record 문법을 사용하면 생성자, getter, equals, hashCode 등이 자동 생성됨
public record HealthResponse(
        // 서버 상태 값
        // 정상 기동 중이면 "ok" 반환
        String status,

        // Health Check 요청 시점의 시간
        // ISO-8601 형식의 문자열로 반환
        String timestamp
) {
}