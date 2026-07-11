package com.example.BE.portfolio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PortfolioDetailResponse(
        Long portfolioId,
        String name,
        int stockCount,
        BigDecimal totalValuation,
        LocalDateTime createdAt,
        List<PortfolioStockItem> stocks
) {
    public record PortfolioStockItem(
            Long stockId,
            String ticker,
            String name,
            String market,
            BigDecimal currentPrice,
            BigDecimal weightPercent
    ) {
    }
}
