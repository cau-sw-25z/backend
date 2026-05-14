package com.example.BE.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "퀀트 프로젝트 API 문서",         //프로젝트 이름을 적습니다
        description = 
            "퀀트 프로젝트의 백엔드 API 명세서입니다.<br>" +   //프로젝트 설명을 적습니다
            "여기에 설명을 쓰라는데 아직 뭘 쓰라는건지 모르겠습니다<br>" +
            "감사합니다 :)", 
        version = "v1.0.0"                      //버전을 적습니다
    )
)
@Configuration

public class SwaggerConfig {
}