package com.example.BE.auth.controller;

import com.example.BE.auth.dto.LoginRequest;
import com.example.BE.auth.dto.TokenResponse;
import com.example.BE.auth.dto.SignupRequest;
import com.example.BE.auth.dto.SignupResponse;
import com.example.BE.auth.service.AuthService;
import com.example.BE.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "회원가입 / 로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임으로 회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(response, "회원가입이 완료되었습니다."));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 JWT accessToken을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인이 완료되었습니다."));
    }
}