package com.example.BE.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ── Common ──────────────────────────────────────────────
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_400", "입력값 검증에 실패했습니다."),

    // ── Auth ────────────────────────────────────────────────
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH_400", "이미 사용 중인 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_401", "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_TOKEN", "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_401_REFRESH", "Refresh Token을 찾을 수 없습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_401_EXPIRED", "Refresh Token이 만료되었습니다. 다시 로그인해 주세요."),

    // ── User ────────────────────────────────────────────────
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "사용자를 찾을 수 없습니다."),

    // ── Stock ───────────────────────────────────────────────
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK_404", "종목을 찾을 수 없습니다."),

    // ── WatchList ───────────────────────────────────────────
    WATCHLIST_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "WATCHLIST_400_DUPLICATE", "이미 관심종목에 등록된 종목입니다."),
    WATCHLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "WATCHLIST_404", "관심종목에서 해당 종목을 찾을 수 없습니다."),

    // ── Portfolio ───────────────────────────────────────────
    PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, "PORTFOLIO_404", "포트폴리오를 찾을 수 없습니다."),
    PORTFOLIO_FORBIDDEN(HttpStatus.FORBIDDEN, "PORTFOLIO_403", "본인의 포트폴리오만 접근할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}