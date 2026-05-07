package com.example.BE.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    // 예외가 발생했을 때 어떤 에러인지 구분하기 위한 ErrorCode
    private final ErrorCode errorCode;

    // ErrorCode를 받아 CustomException을 생성
    public CustomException(ErrorCode errorCode) {
        // RuntimeException의 message 값으로 ErrorCode의 기본 메시지를 전달
        super(errorCode.getMessage());

        // GlobalExceptionHandler에서 사용할 수 있도록 ErrorCode 저장
        this.errorCode = errorCode;
    }
}