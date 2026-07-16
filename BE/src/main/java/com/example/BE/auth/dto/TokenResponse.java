package com.example.BE.auth.dto;

public record TokenResponse(
        String accessToken,
        String tokenType,
        AuthUserResponse user
) {
}