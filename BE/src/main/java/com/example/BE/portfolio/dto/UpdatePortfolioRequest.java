package com.example.BE.portfolio.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePortfolioRequest(

        @NotBlank(message = "name은 필수입니다.")
        String name
) {
}