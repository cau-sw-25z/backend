package com.example.BE.trade.dto;

import com.example.BE.trade.entity.TradeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTradeRequest(

        @NotNull(message = "tradeType은 필수입니다.")
        TradeType tradeType,

        @NotNull(message = "price는 필수입니다.")
        @DecimalMin(value = "0.0", inclusive = false, message = "price는 0보다 커야 합니다.")
        BigDecimal price,

        @NotNull(message = "quantity는 필수입니다.")
        @Positive(message = "quantity는 1 이상의 정수여야 합니다.")
        Integer quantity,

        LocalDateTime tradeDate
) {
}
