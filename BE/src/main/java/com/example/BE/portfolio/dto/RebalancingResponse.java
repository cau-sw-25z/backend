package com.example.BE.portfolio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RebalancingResponse(
        Long portfolioId,
        String portfolioName,
        Integer riskLevel,
        LocalDateTime calculatedAt,
        BigDecimal totalValuation,
        List<RebalancingItem> items
) {

    public record RebalancingItem(
            Long portfolioItemId,
            Long stockId,
            String ticker,
            String name,
            String market,
            BigDecimal avgPrice,
            Integer quantity,
            BigDecimal currentPrice,
            BigDecimal currentWeight,
            BigDecimal recommendedWeight,
            BigDecimal weightDifference,
            RebalancingSuggestion rebalancingSuggestion,
            SignalInfo signal
    ) {
    }

    public record SignalInfo(
            Long id,
            String strategyType,
            String action,
            Double signalValue,
            BigDecimal closePrice,
            LocalDate signalDate,
            LocalDateTime createdAt
    ) {
    }
}