package com.example.BE.portfolio.dto;

import jakarta.validation.constraints.NotBlank;

public record AddPortfolioStockRequest(

        @NotBlank(message = "ticker는 필수입니다.")
        String ticker
) {
}