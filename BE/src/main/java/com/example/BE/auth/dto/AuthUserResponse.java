package com.example.BE.auth.dto;

public record AuthUserResponse(
        Long userId,
        String email,
        String nickname
) {
}