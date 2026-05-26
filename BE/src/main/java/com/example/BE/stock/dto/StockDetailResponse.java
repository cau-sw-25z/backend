package com.example.BE.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// 종목 상세 조회 응답 (기본 정보 + 최근 30일 가격 이력)
public record StockDetailResponse(
        Long id,
        String ticker,
        String name,
        String market,
        List<PriceItem> priceHistory
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