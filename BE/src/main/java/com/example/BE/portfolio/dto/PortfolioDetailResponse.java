package com.example.BE.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioDetailResponse(
        Long portfolioId,
        String name,
        int stockCount,
        BigDecimal totalValuation,
        List<PortfolioItemDetail> items
) {
    public record PortfolioItemDetail(
            Long stockId,
            String ticker,
            String name,
            String market,
            BigDecimal avgPrice,
            Integer quantity,
            BigDecimal currentPrice,
            BigDecimal weightPercent
    ) {
    }
}
