package com.example.BE.portfolio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PortfolioSummaryResponse(
        Long portfolioId,
        String name,
        int stockCount,
        BigDecimal totalValuation
) {
}
