package com.example.BE.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// 가격 이력 조회 응답 (기간 지정)
public record PriceHistoryResponse(
        String ticker,
        LocalDate from,
        LocalDate to,
        List<PriceItem> prices
) {
    public record PriceItem(
            LocalDate date,
            BigDecimal openPrice,
            BigDecimal closePrice,
            BigDecimal highPrice,
            BigDecimal lowPrice,
            Long volume
    ) {}
}