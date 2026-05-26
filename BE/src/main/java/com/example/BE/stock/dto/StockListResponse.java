package com.example.BE.stock.dto;

import java.math.BigDecimal;
import java.util.List;

// 종목 목록 조회 응답
// nextCursor가 null이면 마지막 페이지
public record StockListResponse(
        List<StockItem> items,
        Long nextCursor,
        boolean hasNext
) {
    public record StockItem(
            Long id,
            String ticker,
            String name,
            String market,
            BigDecimal closePrice,   // 최근 종가
            BigDecimal changeRate    // 전일 대비 등락률 (%)
    ) {}
}