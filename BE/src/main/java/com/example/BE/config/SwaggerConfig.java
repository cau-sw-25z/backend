package com.example.BE.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

// Swagger 문서의 기본 정보를 설정하는 클래스
@OpenAPIDefinition(
        info = @Info(
                // Swagger UI 상단에 표시될 프로젝트명
                title = "BE API",

                // API 문서 버전
                version = "v1.0.0",

                // API 문서 설명
                description = "Backend API Documentation"
        )
)
@Configuration
public class SwaggerConfig {
}