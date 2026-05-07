package com.example.BE.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request: 잘못된 요청
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),

    // 401 Unauthorized: 인증이 필요한 요청
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),

    // 403 Forbidden: 인증은 되었지만 접근 권한이 없는 요청
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "접근 권한이 없습니다."),

    // 404 Not Found: 요청한 리소스를 찾을 수 없는 경우
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "요청한 리소스를 찾을 수 없습니다."),

    // 500 Internal Server Error: 예상하지 못한 서버 내부 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다."),

    // 400 Bad Request: @Valid 검증 실패
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_400", "입력값 검증에 실패했습니다.");

    // 실제 HTTP 상태 코드
    private final HttpStatus httpStatus;

    // 프론트엔드에서 에러 종류를 구분할 수 있는 커스텀 코드
    private final String code;

    // 사용자 또는 프론트엔드에 전달할 기본 에러 메시지
    private final String message;
}