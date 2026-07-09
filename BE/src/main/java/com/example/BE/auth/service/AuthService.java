package com.example.BE.auth.service;

import com.example.BE.auth.dto.AuthUserResponse;
import com.example.BE.auth.dto.LoginRequest;
import com.example.BE.auth.dto.TokenResponse;
import com.example.BE.auth.dto.SignupRequest;
import com.example.BE.auth.dto.SignupResponse;
import com.example.BE.common.exception.CustomException;
import com.example.BE.common.exception.ErrorCode;
import com.example.BE.config.JwtUtil;
import com.example.BE.entity.User;
import com.example.BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String email = normalizeEmail(request.email());
        String nickname = request.nickname().trim();

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = userRepository.save(
                new User(email, nickname, encodedPassword)
        );

        return new SignupResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }

    public TokenResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());

        AuthUserResponse userResponse = new AuthUserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );

        return new TokenResponse(
                accessToken,
                "Bearer",
                userResponse
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}