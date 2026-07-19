package com.example.BE.portfolio.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PortfolioItemRequest(

        @NotBlank(message = "ticker는 필수입니다.")
        String ticker,

        @NotNull(message = "avgPrice는 필수입니다.")
        @DecimalMin(value = "0", inclusive = false, message = "avgPrice는 0보다 커야 합니다.")
        BigDecimal avgPrice,

        @NotNull(message = "quantity는 필수입니다.")
        @Positive(message = "quantity는 1 이상이어야 합니다.")
        Integer quantity
) {
}