package com.example.BE.trade.dto;

import com.example.BE.trade.entity.Trade;
import com.example.BE.trade.entity.TradeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeResponse(
        Long id,
        Long portfolioId,
        Long portfolioItemId,
        String ticker,
        TradeType tradeType,
        BigDecimal price,
        Integer quantity,
        LocalDateTime tradeDate,
        BigDecimal currentAvgPrice,
        Integer currentQuantity
) {
    public static TradeResponse from(Trade trade) {
        return new TradeResponse(
                trade.getId(),
                trade.getPortfolioItem().getPortfolio().getId(),
                trade.getPortfolioItem().getId(),
                trade.getPortfolioItem().getStock().getTicker(),
                trade.getTradeType(),
                trade.getPrice(),
                trade.getQuantity(),
                trade.getTradeDate(),
                trade.getPortfolioItem().getAvgPrice(),
                trade.getPortfolioItem().getQuantity()
        );
    }
}
