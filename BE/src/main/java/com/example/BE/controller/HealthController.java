package com.example.BE.controller;

import com.example.BE.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

// Swagger 문서에서 이 컨트롤러를 Health 그룹으로 묶기 위한 태그
@Tag(name = "Health", description = "서버 상태 확인 API")
@RestController
public class HealthController {

    // Swagger 문서에 표시될 API 설명
    @Operation(summary = "Health Check", description = "서버 기동 상태를 확인합니다.")

    // GET /api/health 요청을 처리하는 엔드포인트
    @GetMapping("/api/health")
    public HealthResponse healthCheck() {
        // 서버가 정상적으로 동작 중이면 status는 "ok"로 반환
        // timestamp는 현재 시간을 문자열로 반환
        return new HealthResponse("ok", Instant.now().toString());
    }
}