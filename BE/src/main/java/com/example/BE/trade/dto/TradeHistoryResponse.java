package com.example.BE.trade.dto;

import com.example.BE.portfolio.entity.PortfolioItem;
import com.example.BE.trade.entity.Trade;
import com.example.BE.trade.entity.TradeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TradeHistoryResponse(
        Long portfolioId,
        Long portfolioItemId,
        String ticker,
        BigDecimal currentAvgPrice,
        Integer currentQuantity,
        int count,
        List<TradeItem> trades
) {
    public static TradeHistoryResponse of(PortfolioItem portfolioItem, List<Trade> trades) {
        List<TradeItem> items = trades.stream()
                .map(TradeItem::from)
                .toList();

        return new TradeHistoryResponse(
                portfolioItem.getPortfolio().getId(),
                portfolioItem.getId(),
                portfolioItem.getStock().getTicker(),
                portfolioItem.getAvgPrice(),
                portfolioItem.getQuantity(),
                items.size(),
                items
        );
    }

    public record TradeItem(
            Long id,
            TradeType tradeType,
            BigDecimal price,
            Integer quantity,
            LocalDateTime tradeDate
    ) {
        public static TradeItem from(Trade trade) {
            return new TradeItem(
                    trade.getId(),
                    trade.getTradeType(),
                    trade.getPrice(),
                    trade.getQuantity(),
                    trade.getTradeDate()
            );
        }
    }
}
